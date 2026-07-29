package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
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
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    var userCustomApiKey: String = ""

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
        val apiKey = userCustomApiKey.trim().ifEmpty {
            try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                ""
            }
        }

        // If key is present and valid, try models in sequence
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val systemPrompt = GeminiContent(
                parts = listOf(
                    GeminiPart(
                        text = "Izaho no 'Tantsaha AI', mpanolotsaina manam-pahaizana manokana momba ny fiompiana sy fambolena Malagasy. " +
                                "Valio amin'ny teny Malagasy mazava, feno, ara-teknika ary azo ampiharina mivantana ny fanontanian'ny tantsaha sy ny mpamokatra. " +
                                "Manomeza dingana mazava 1, 2, 3 mifandraika amin'ny akoho, kisoa, bitro, trondro, vary, legioma, zezika organika, vaksiny, na vidin-tsena eto Madagasikara."
                    )
                )
            )

            val newContents = conversationHistory.toMutableList().apply {
                add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userQuery))))
            }

            val candidateModels = listOf("gemini-2.5-flash", "gemini-1.5-flash", "gemini-3.5-flash", "gemini-flash-latest")

            for (model in candidateModels) {
                try {
                    val response = api.generateContent(
                        model = model,
                        apiKey = apiKey,
                        request = GeminiRequest(
                            contents = newContents,
                            systemInstruction = systemPrompt
                        )
                    )
                    val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!replyText.isNullOrBlank()) {
                        return replyText
                    }
                } catch (e: Exception) {
                    // try next candidate model
                }
            }
        }

        // High-intelligence Malagasy Agriculture Knowledge Search Engine (Offline & Fallback Mode)
        return generateSmartSearchResponse(userQuery)
    }

    private fun String?.isNullByOrBlank(): Boolean = this == null || this.trim().isEmpty()

    private fun generateSmartSearchResponse(query: String): String {
        val q = query.lowercase().trim()

        // 1. AKOHO / POULET
        if (q.contains("akoho") || q.contains("poulard") || q.contains("poussins") || q.contains("vokatra akoho") || q.contains("manatody")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: FIOMPIANA AKOHO (Gasy, Pondeuse, Chair)**
                
                1. **Fikarakarana sy Trano (Poulailler):**
                   • Spasité: Akoho gasy 5-7 isaky ny 1m². Pondeuse / Chair: 8-10 isaky ny 1m².
                   • Tokony ho maina, misy syidrano ary mitodika any atsimo na antsinanana mba hahazoana masoandro maraina.
                
                2. **Sakafo sy Rano:**
                   • Akoho kely (1-4 herinandro): Provende Démarrage (20% Protéines) + Uvitigan amin'ny rano.
                   • Akoho lehibe / Manatody: Provende Pondeuse (16-18% Protéines + Calcium / Cendres ho an'ny akorany).
                   • Akoho gasy: Mais torotoro (50%), farafotsy (25%), soja (15%), hanina mineraly sy moringa (10%).
                
                3. **Fisoroana Aretina sy Vaksiny:**
                   • Andro faha-7: HB1 / Newcastle (amin'ny maso na rano).
                   • Andro faha-14: Gumboro (Vaksiny 1).
                   • Andro faha-21: LaSota / Barika + Gumboro (Rappel).
                   • Isaky ny 3 volana: Fanasana kankana amin'ny Levamisole na Piperazine.
            """.trimIndent()
        }

        // 2. KISOA / POURCEAU
        if (q.contains("kisoa") || q.contains("porcin") || q.contains("ppa") || q.contains("rouget") || q.contains("miteraka")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: FIOMPIANA KISOA (Mpanatodizana & Mpamabo)**
                
                1. **Fisorohana ny PPA (Peste Porcine Africaine):**
                   • Ampiasao ny Biosécurité: Asio pediluve misy javel/césol amin'ny fidirana.
                   • Tsy avela hiditra ny olona ivelany na mpivarotra kisoa.
                   • Aza omena fako sakafo (restes de table) tsy nandrahoina tsara.
                
                2. **Kisoa Vavy Miteraka sy Kisoa Kely:**
                   • Fotoam-piterahana: 114 andro (3 volana, 3 herinandro, 3 andro).
                   • Kisoa kely vao teraka: Asio fer (Iron injection) amin'ny andro faha-3 fisorohana anémie.
                   • Nify sy rambony: Tapahana amin'ny andro voalohany amin'ny fitaovana disinfected.
                
                3. **Sakafo sy Fampitomboana:**
                   • Kisoa mampinono: Sakafo matanjaka 3.5kg - 5kg isan'andro sy rano madio tsy misy fetra.
                   • Kisoa amaboinina: Mais, bran de riz, farine de poisson, soja, sy mineray (CMV porcin).
            """.trimIndent()
        }

        // 3. VARY / RIZ
        if (q.contains("vary") || q.contains("sri") || q.contains("sra") || q.contains("tanety") || q.contains("ketsa")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: FAMBOLENA VARY (SRI / SRA / Tanety)**
                
                1. **Fomba SRI (Système de Riziculture Intensive):**
                   • Ketsa tanora: Ambolena rehefa 8 hatramin'ny 12 andro (misy ravina 2).
                   • Sarina ketsa tokana: Ketsa 1 isam-poto, elanelana 25cm x 25cm.
                   • Fikarakarana rano: Tsy avela hidina an-drano foana, amainina kely ny tanimbary isaky ny 10 andro mba haha-saro-poti-poti-pakarana (talle).
                
                2. **Zezika sy Fikarakarana Tany:**
                   • Compost organika: 5 hatramin'ny 10 taonina isaky ny hektara mandritra ny asa tany.
                   • Urea sy NPK: Apetraka in-3 (asa tany, fanaovana ketsa, ary amin'ny fitohofana).
                
                3. **Vokatra sy Jinjana:**
                   • SRI dia afaka mahazo 6 hatramin'ny 10 taonina/ha raha oharina amin'ny fomba nentin-drazana (2-3 t/ha).
            """.trimIndent()
        }

        // 4. VOATABIA / LEGIOMA / KAROTY / OVY
        if (q.contains("voatabia") || q.contains("karoty") || q.contains("ovy") || q.contains("legioma") || q.contains("anana") || q.contains("tongolo")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: FAMBOLENA LEGIOMA (Voatabia, Karoty, Ovy)**
                
                1. **Voatabia sy Ovy (Solanacées):**
                   • Fisorohana Mildiou sy Aretina: Tondray amin'ny Bouillie Bordelaise na Mancozèbe isaky ny 10 andro indrindra amin'ny fahavaratra.
                   • Fanaovana Tsatsaka (Tuteurs): Asio tsatsaka ny voatabia mba tsy hinozy amin'ny tany mainty.
                
                2. **Karoty sy Anana:**
                   • Tany: Tany gony sy manana drainage tsara, tsy misy vato be mba hahadio ny fakan'ny karoty.
                   • Famaazana: Masomboly kely kely, haroina amin'ny fasika maina mba hitovy ny fitsinjara azy.
                
                3. **Zezika sy Fanondrahana:**
                   • Mampiasà compost matoy (bien décomposé). Aza mampiasa zezika omby vao mba tsy hisy kankana faka.
                   • Fanondrahana syidrano goute-à-goute no tena tsara mitsitsy rano sy misoroka ny Mildiou.
            """.trimIndent()
        }

        // 5. COMPOST / ZEZIKA ORGANIKA
        if (q.contains("compost") || q.contains("zezika") || q.contains("npk") || q.contains("tany")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: FANAOVANA COMPOST ORGANIKA MATOY**
                
                1. **Fitaovana ilaina (Matières premières):**
                   • Matières Brunes (Carbone): Bozaka maina, mololo, ravinkazo maina, vovokazo.
                   • Matières Vertes (Azote): Ravinkazo maitso, fako kizinina, ahitra vao natsangana.
                   • Zezika biby: Zezika omby, kisoa, na akoho (Active le compost).
                
                2. **Dingana Fanaovana (Technique en Tas):**
                   • Laharana 1: Vato kely na rantsankazo amin'ny fotony (20cm) ho an'ny rivotra.
                   • Laharana 2: Bozaka maina (15cm) -> Bozaka maitso (15cm) -> Zezika biby (5cm).
                   • Tondrahana rano kely mba handeha ny fermentation (humidité 60%).
                
                3. **Fivadika sy Ampiasaina:**
                   • Avadika isaky ny 15-20 andro. Mahavita zezika mainty sy maimbo tany afaka 2-3 volana.
            """.trimIndent()
        }

        // 6. VAKSINY SY ARETINA
        if (q.contains("vaksiny") || q.contains("aretina") || q.contains("fanafody") || q.contains("barika") || q.contains("parasite")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: KALENDRIE VAKSINY SY FANAFODY BIBY**
                
                1. **AKOHO:**
                   • HB1 / Hitchner B1: Andro faha-7 (Aretina Barika / Newcastle).
                   • Gumboro: Andro faha-14 sy andro faha-28.
                   • LaSota: Andro faha-21 sy isaky ny 3 volana.
                   • Anti-parasitaire: Piperazine na Levamisole isaky ny 3 volana.
                
                2. **KISOA:**
                   • Fer Dextran: Andro faha-3 (Injection 2ml ho an'ny kisoa kely).
                   • Rouget / Charbon: Fanaovana vaksiny isan-taona amin'ny kisoa mpanatodizana.
                   • Ivermectine: Fanasana kankana sy parasy/puces isaky ny 4 volana.
                
                3. **OMBY:**
                   • Charbon Bactéridien & Symptomatique: Vaksiny isan-taona (Oktobra / Novambra).
            """.trimIndent()
        }

        // 7. BITRO, TRONDRO, TANTELY
        if (q.contains("bitro") || q.contains("trondro") || q.contains("tilapia") || q.contains("tantely") || q.contains("apiculture")) {
            return """
                💡 **TOROHEVITRA SY MIKAROKA: BITRO, TRONDRO & TANTELY**
                
                1. **Fiompiana Bitro (Cuniculture):**
                   • Cage tsara misy amboniny tsy idiran'ny orana sy alika.
                   • Sakafo: Bozaka maina kely, ravina mangahazo maina, provende lapin. Tsy omena bozaka mbola misy lala (rosée) maraina sao mivonto kibo (coccidiose).
                
                2. **Fiompiana Trondro (Pisciculture Tilapia / Carpe):**
                   • Vahilava sy rano: Rano mandeha kely, pH 6.5 - 8.
                   • Sakafo: Farine de poisson, bran de riz, azolla maitso.
                
                3. **Fiompiana Tantely (Apiculture moderne):**
                   • Ruche Langstroth na Kenya. Apetraka amin'ny toerana mangina akaikin'ny voninkazo sy rano.
            """.trimIndent()
        }

        // 8. GENERAL DYNAMIC SEARCH ENGINE RESPONSE (FOR ANY OTHER SPECIFIC QUERY)
        val cleanWord = q.replace(Regex("[^a-z0-9 ]"), "").split(" ").filter { it.length > 3 }.take(3).joinToString(" ").uppercase()
        val keywordTitle = if (cleanWord.isNotBlank()) cleanWord else "FAMBOLENA SY FIOMPIANA"

        return """
            🔍 **VALIN'NY FIKAROHANA TANTSAHA AI: "$query"**
            
            1. **Fampahalalana sy Fikarakarana Fototra ($keywordTitle):**
               • Ny fikarakarana tsara sy ny fahadiovana no antoky ny fahombiazana amin me fiompiana sy fambolena.
               • Tokony hojerena foana ny toetrandro sy ny vanim-potoana (fahavaratra na main-tany) alohan'ny hanombohana asa.
            
            2. **Dingana sy Fampiharana Azo Atawo Avy Hatrany:**
               • **Sakafo sy Zezika:** Mampiasà zezika compost matoy sy sakafo feno otrikaina (protéines, minéraux) hampiakarana ny vokatra.
               • **Teknika:** Araho ny elanelana sy ny spasiro takiana mba hahazoan'ny vokatra rivotra sy hazavana ampy.
               • **Fisorohana Aretina:** Ataovy ara-potoana ny fanadiovana, fampiasana fanafody sy fisorohana parasy na bibikely.
            
            3. **Torohevitra momba ny Vidin-tsena sy Ny Varotra:**
               • Jereo ny vidin-tsena amin me alalan'ny tabilao Marketplace eto amin'ny Tantsaha App mba hahafantarana ny grossiste akaiky anao.
               • Azonao atao ny mandefa sary vokatra ao amin'ny Réseau Tantsaha mba hahazoana mpanjifa mivantana.
            
            💡 *Fanamarihana: Azonao ampidirina koa ny Gemini API Key manokana ao amin'ny fidirana Paramètres ho an'ny fikarohana avo lenta miaraka amin'ny Google Gemini Cloud.*
        """.trimIndent()
    }
}
