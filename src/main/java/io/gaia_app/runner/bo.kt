package io.gaia_app.runner

data class RunnerStep(val id: String, val image: String, val script: String, val env: List<String>)