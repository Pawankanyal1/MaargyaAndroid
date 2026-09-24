package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.NetworkResult
import com.example.data.repository.HandwritingRepository
import com.example.domain.model.HandwritingReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class HandwritingAnalysisStep(val label: String, val progress: Float) {
    UPLOADING("Uploading handwriting image sample...", 0.2f),
    CONTOUR_SCANNING("Extracting stroke contours, baseline & slant angles...", 0.5f),
    MEASURING_METRICS("Evaluating letter spacing, pressure gradients & margins...", 0.8f),
    GENERATING_REPORT("Compiling self-reflection observations & summary...", 1.0f)
}

data class HandwritingUiState(
    val sampleImageUri: String? = null,
    val isAnalyzing: Boolean = false,
    val currentStep: HandwritingAnalysisStep = HandwritingAnalysisStep.UPLOADING,
    val report: HandwritingReport? = null,
    val isReportSaved: Boolean = false,
    val errorMessage: String? = null
)

class HandwritingViewModel(
    private val repository: HandwritingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HandwritingUiState())
    val uiState: StateFlow<HandwritingUiState> = _uiState.asStateFlow()

    fun setSampleImage(uriString: String) {
        _uiState.value = _uiState.value.copy(
            sampleImageUri = uriString,
            errorMessage = null
        )
    }

    fun startAnalysis(onAnalysisFinished: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAnalyzing = true,
                errorMessage = null
            )

            // Multi-step progressive animation
            HandwritingAnalysisStep.entries.forEach { step ->
                _uiState.value = _uiState.value.copy(currentStep = step)
                delay(450)
            }

            val uri = _uiState.value.sampleImageUri ?: "default_sample"
            when (val result = repository.analyzeSample(uri)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        report = result.data
                    )
                    onAnalysisFinished()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAnalyzing = false,
                        errorMessage = result.message
                    )
                }
                NetworkResult.Loading -> {}
            }
        }
    }

    fun saveCurrentReport() {
        val report = _uiState.value.report ?: return
        viewModelScope.launch {
            repository.saveReport(report)
            _uiState.value = _uiState.value.copy(isReportSaved = true)
        }
    }

    fun reset() {
        _uiState.value = HandwritingUiState()
    }
}
