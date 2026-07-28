package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun askTantsahaAi(userQuery: String, conversationHistory: List<GeminiContent> = emptyList()): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateOfflineFallbackResponse(userQuery)
        }

        val systemPrompt = GeminiContent(
            parts = listOf(
                GeminiPart(
                    text = "Lasa mpanolotsaina manam-pahaizana manokana momba ny fiompiana sy fambolena Malagasy ianao antsoina hoe 'Tantsaha AI'. " +
                            "Valio amin'ny teny Malagasy tsotra, mazava sy azo ampiharina tsara avy hatrany ny fanontanian'ny tantsaha. " +
                            "Omeo torohevitra momba ny fiompiana (akoho, kisoa, bitro, tantely, trondro) sy fambolena (vary, legioma, voankazo, compost, tany) araka ny toetrandro sy ny zava-misy eto Madagasikara."
                )
            )
        )

        val newContents = conversationHistory.toMutableList().apply {
            add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userQuery))))
        }

        return try {
            val response = api.generateContent(
                apiKey = apiKey,
                request = GeminiRequest(
                    contents = newContents,
                    systemInstruction = systemPrompt
                )
            )
            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            replyText ?: "Afiofio fialantsiny, mbola tsy azoko tsara ny fanontanianao. Avereno ampiasaina azafady!"
        } catch (e: Exception) {
            generateOfflineFallbackResponse(userQuery)
        }
    }

    private fun generateOfflineFallbackResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("akoho") -> "Momba ny Akoho Gasy / Poulet de Chair:\n1. Omeo trano madio sy maina misy rivotra miditra tsara.\n2. Vaksiny tena ilaina: HB1 amin'ny andro faha-7, LaSota amin'ny faha-21 andro ho an'ny aretina Barika (Newcastle).\n3. Sakafo: Mais, farafotsy, soja, ary hanina mineraly ampy."
            q.contains("kisoa") -> "Momba ny Fiompiana Kisoa:\n1. Fisorohana Pestes Porcine (PPA): Tsy avela hiditra ny olon-ko azy, disinfected ny fidirana.\n2. Fanasana kankana (Déparasitage) isaky ny 3-4 mois.\n3. Sakafo feno provende mifandray amin'ny taon'ny kisoa."
            q.contains("vary") -> "Momba ny Fambolena Vary (SRI / SRA):\n1. Fambolena ketsa tanora (8-12 andro ho an'ny SRI).\n2. Zezika organika ampy (Compost 5-10 taonina/ha) amin'ny fikarakarana tany.\n3. Sarina ketsa tokana sady malalaka (25cm x 25cm)."
            q.contains("compost") || q.contains("zezika") -> "Fanaovana Compost Organika:\n1. Laharana bozaka maina, bozaka maitso, ary zezika biby.\n2. Tondrahana rano kely mba handeha ny fermentation.\n3. Avadika isaky ny 15 andro. Mahavita zezika tsara afaka 2-3 volana."
            q.contains("vaksiny") -> "Lisitry ny Vaksiny fototra:\n- Akoho: HB1 (Newcastle), Gumboro, Peste Aviaire.\n- Kisoa: Rouget, Charbon.\n- Omby: Charbon bactéridien sy symptomatic."
            else -> "Manohatra Tantsaha! Indreto torohevitra fototra momba ny fiompiana sy fambolena:\n- Huile de coude sy fikarakarana ara-potoana no antoky ny fahombiazana.\n- Mampiasà zezika organika compost hanatsarana ny tany.\n- Zavao hatrany ny daty fanaovana vaksiny biby hanoherana ny aretina."
        }
    }
}
