package io.gaia_app.runner

import io.gaia_app.stacks.bo.Step

data class RunnerStep(val step: Step, val image: String, val script: String, val env: List<String>)