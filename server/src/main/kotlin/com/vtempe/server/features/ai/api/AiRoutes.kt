package com.vtempe.server.features.ai.api

import com.vtempe.server.features.ai.domain.usecase.BootstrapUseCase
import com.vtempe.server.features.ai.domain.usecase.ChatUseCase
import com.vtempe.server.features.ai.domain.usecase.NutritionUseCase
import com.vtempe.server.features.ai.domain.usecase.SleepUseCase
import com.vtempe.server.features.ai.domain.usecase.TrainingUseCase
import com.vtempe.server.shared.dto.advice.AiAdviceRequest
import com.vtempe.server.shared.dto.bootstrap.AiBootstrapRequest
import com.vtempe.server.shared.dto.chat.AiChatRequest
import com.vtempe.server.shared.dto.nutrition.AiNutritionRequest
import com.vtempe.server.shared.dto.training.AiTrainingRequest
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.registerAiRoutes() {
    val training: TrainingUseCase by inject()
    val nutrition: NutritionUseCase by inject()
    val sleep: SleepUseCase by inject()
    val chat: ChatUseCase by inject()
    val bootstrap: BootstrapUseCase by inject()

    route("/ai") {
        post("/training") {
            val req = call.receive<AiTrainingRequest>()
            call.respond(training.execute(req))
        }
        post("/nutrition") {
            val req = call.receive<AiNutritionRequest>()
            call.respond(nutrition.execute(req))
        }
        post("/sleep") {
            val req = call.receive<AiAdviceRequest>()
            call.respond(sleep.execute(req))
        }
        post("/chat") {
            val req = call.receive<AiChatRequest>()
            call.respond(chat.execute(req))
        }
        post("/bootstrap") {
            val req = call.receive<AiBootstrapRequest>()
            call.respond(bootstrap.execute(req))
        }
    }
}
