package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class TantsahaRepository(
    private val vaccineDao: VaccineDao,
    private val bookmarkDao: BookmarkDao,
    private val farmerNoteDao: FarmerNoteDao
) {

    // Room DB Flow Streams
    val savedSchedules: Flow<List<VaccineScheduleEntity>> = vaccineDao.getAllVaccineSchedules()
    val savedBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
    val savedNotes: Flow<List<FarmerNoteEntity>> = farmerNoteDao.getAllNotes()

    suspend fun saveVaccineSchedule(animalType: String, batchName: String, vaccineName: String, date: String, count: Int, notes: String) {
        vaccineDao.insertSchedule(
            VaccineScheduleEntity(
                animalType = animalType,
                batchName = batchName,
                vaccineName = vaccineName,
                scheduledDate = date,
                status = "Mbola hovana",
                animalCount = count,
                notes = notes
            )
        )
    }

    suspend fun markScheduleDone(id: Long) {
        vaccineDao.updateScheduleStatus(id, "Vita")
    }

    suspend fun deleteSchedule(schedule: VaccineScheduleEntity) {
        vaccineDao.deleteSchedule(schedule)
    }

    fun isBookmarked(bookId: String): Flow<Boolean> = bookmarkDao.isBookmarked(bookId)

    suspend fun toggleBookmark(bookId: String, title: String, category: String, currentStatus: Boolean) {
        if (currentStatus) {
            bookmarkDao.deleteBookmarkById(bookId)
        } else {
            bookmarkDao.insertBookmark(BookmarkEntity(bookId = bookId, title = title, category = category))
        }
    }

    suspend fun saveFarmerNote(title: String, category: String, content: String) {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        farmerNoteDao.insertNote(FarmerNoteEntity(title = title, category = category, content = content, dateCreated = dateStr))
    }

    suspend fun deleteNote(note: FarmerNoteEntity) {
        farmerNoteDao.deleteNote(note)
    }

    // Static Domain Content Providers
    fun getLivestockCategories(): List<LivestockCategory> = listOf(
        LivestockCategory(
            id = "akoho_gasy",
            name = "Akoho Gasy",
            description = "Fiompiana akoho gasy nohatsaraina (akohokely, akoho manatody, akoho mpanatodizana)",
            iconName = "Pets",
            topics = listOf(
                LivestockTopic(
                    id = "akoho_fomba",
                    title = "Fomba Fiompiana",
                    subtitle = "Teknika nohatsaraina sy fanaraha-maso",
                    details = "Ny fiompiana akoho gasy nohatsaraina dia mampitombo ny taham-pahaveloman'ny akohokely hatramin'ny 90%. Zava-dehibe ny fampidinana ny fahafatesana vokatry ny mangatsiaka sy ny aretina amin'ny alalan'ny fisarahana ny reny sy ny akohokely rehefa afaka 14 andro.",
                    keyTips = listOf("Saroho ny fitaizana akohokely amin'ny fitaovana fanafanana (poêle / amponga kely)", "Atsaharo ny famelana azy ireo hifandray amin'ny akoho ivelany am-boalohany", "Fidiran'ny hazavana ampy sy rivotra madio")
                ),
                LivestockTopic(
                    id = "akoho_trano",
                    title = "Trano Fiompiana",
                    subtitle = "Fanaovana trano madio, maina sy mifanaraka amin'ny rivotra",
                    details = "Ataovy avo amin'ny tany ny gorodona (50cm hatramin'ny 1m) mba tsy hidiran'ny hamandoana. Ampiasao ny trano vato na zozoro misy valindrihana mololo. Velarana: akoho 5 hatramin'ny 7 isaky ny metatra tora-droa (m²).",
                    keyTips = listOf("Apetraho amin'ny toerana tsy azon'ny rivotra mahery", "Ataovy vovoka tsofa na mololo madio ny gorodona", "Disinfect-eo amin'ny vovoka chaux isaky ny 2 herinandro")
                ),
                LivestockTopic(
                    id = "akoho_sakafo",
                    title = "Sakafo & Fahanana",
                    subtitle = "Kajy sakafo feno otrikaina hampiakatra ny vokatra",
                    details = "Sakafo hanoherana ny hanoanana sy hampitomboana: Mais totoy (55%), Soja na Tournesol (20%), Farafotsy (15%), Poudre d'os/coquillage (8%), Sira sy Premix mineraly (2%).",
                    keyTips = listOf("Akohokely (0-4 herinandro): Provende Démarrage (15g-35g/andro)", "Akoho lehibe: 100g-120g isan'andro misy rano madio tsy tapaka", "Omeo bozaka maitso toy ny ananambo (Moringa) ho otrikaina")
                ),
                LivestockTopic(
                    id = "akoho_fiterahana",
                    title = "Fiterahana & Atody",
                    subtitle = "Fitantanana ny fanatodizana sy fikarakarana atody",
                    details = "Aparitaho ny fitaizam-batana sy ny akany fanatodizana. Apetraho amin'ny toerana mangina ny akany. Fidio ny atody lehibe sy madio ho ho incubator na hoizao amin'ny akoho mpanatody.",
                    keyTips = listOf("Akany 1 ho an'ny akoho 4 manatody", "Aza sasana amin'ny rano ny atody ifidiana hakatona", "Tahirizo amin'ny toerana mangatsiatsiaka (15°C - 18°C)")
                ),
                LivestockTopic(
                    id = "akoho_fisorohana",
                    title = "Fisorohana Aretina",
                    subtitle = "Aretina Newcastle (Barika), Gumboro sy Viriosy",
                    details = "Ny Newcastle (Barika) no mamono akoho be indrindra eto Madagasikara. Ny fisorohana no lalana tokana: fahadiovana sy vaksiny ara-potoana.",
                    keyTips = listOf("Aza ampidirina avy hatrany ny akoho vaovao novidina am-tsena (Quarantaine 14 andro)", "Fero ny fidirana amin'ny trano fiompiana", "Atsaharo ny fampiasana rano maloto")
                ),
                LivestockTopic(
                    id = "akoho_vaksiny",
                    title = "Vaksiny Ara-potoana",
                    subtitle = "Kalandrie fanaovana vaksiny akoho gasy",
                    details = "Day 7: HB1 (Barika / Newcastle) - dobo maso na vava.\nDay 14: Gumboro (IBD).\nDay 21: LaSota (Newcastle rappel).\nDay 45: Peste Aviaire & Variole (Picotage).",
                    keyTips = listOf("Ataovy amin'ny rano tsy misy chlore ny vaksiny", "Tahirizo ao amin'ny mangatsiaka (2°C - 8°C)", "Vaksino fony salama tsara ny akoho")
                ),
                LivestockTopic(
                    id = "akoho_kajy",
                    title = "Kajy Tombony",
                    subtitle = "Fombafomba fikajiana tombony amin'ny akoho 100",
                    details = "Vidin'akohokely 100 x 2,500 Ar = 250,000 Ar.\nSakafo mandritra ny 4 volana = 1,200,000 Ar.\nVaksiny sy fanafody = 80,000 Ar.\nTotal sarany: 1,530,000 Ar.\nVara-mora akoho 90 velona x 25,000 Ar = 2,250,000 Ar.\nTombony madio: 720,000 Ar!",
                    keyTips = listOf("Kajio hatrany ny taham-pahafatesana (Mortalité < 10%)", "Vidion'ny sakafo am-mora rehefa taom-ijinjana", "Amidio mialoha ny fety lehibe")
                )
            )
        ),
        LivestockCategory(
            id = "poulet_de_chair",
            name = "Poulet de Chair",
            description = "Fiompiana akoho amam-borona ho amin'ny hena mandritra ny 42 andro",
            iconName = "Restaurant",
            topics = listOf(
                LivestockTopic(
                    id = "chair_fomba",
                    title = "Fomba Fiompiana",
                    subtitle = "Fitaizana mifantoka amin'ny fitomboana haingana (42 andro)",
                    details = "Ny Poulet de Chair dia mila fiheverana manokana momba ny hafanana am-boalohany sy ny fikarakarana tsara. Ao anatin'ny 6 herinandro dia mahatratra 2.2kg hatramin'ny 2.5kg ny lanjany.",
                    keyTips = listOf("Hafanana amin'ny herinandro 1: 32°C - 35°C", "Rano madio misy suavit sy vitamines amin'ny 3 andro voalohany", "Aza avela hisy tabataba mahery amin'ny trano")
                ),
                LivestockTopic(
                    id = "chair_sakafo",
                    title = "Sakafo & Fahanana",
                    subtitle = "Démarrage, Croissance, sy Finition",
                    details = "Day 1-14: Provende Démarrage (23% protéine).\nDay 15-28: Provende Croissance (20% protéine).\nDay 29-42: Provende Finition (18% protéine). Total sakafo isan'akoho: 4.2 kg.",
                    keyTips = listOf("Indice de Conversion (IC) tanjona: 1.7 hatramin'ny 1.9", "Rano madio in-2 avo heny amin'ny sakafo lany", "Tehirizo amin'ny toerana maina ny sakafo")
                ),
                LivestockTopic(
                    id = "chair_trano",
                    title = "Trano & Ventilation",
                    subtitle = "Fandaminana trano akoho hena",
                    details = "Ataovy 10 hatramin'ny 12 akoho isaky ny m² amin'ny dingana famaranana. Mololo na vovoka tsofa 10cm ny hateviny. Rivotra miditra sy mivoaka tsy tapaka.",
                    keyTips = listOf("Aza avela ho lena ny litière", "Ampiasao ny fitaovam-pahanana automatique raha azo atao", "Hazavana 23 ora isan'andro am-boalohany")
                )
            )
        ),
        LivestockCategory(
            id = "kisoa",
            name = "Fiompiana Kisoa",
            description = "Teknika fiompiana kisoa manatodidizina sy kisoa mampavitrika",
            iconName = "Pets",
            topics = listOf(
                LivestockTopic(
                    id = "kisoa_fomba",
                    title = "Fomba Fiompiana",
                    subtitle = "Safidy kisoa vavy sy lahy fiompiana",
                    details = "Safidio ny karazana Large White, Landrace na Pietrain. Kisoa vavy salama sy misy nono 12 hatramin'ny 14 farafahakeliny. Kisoa lahy matanjaka tsy havana akaiky.",
                    keyTips = listOf("Taona fampiterahana voalohany: 8-9 volana (lanja 110kg)", "Aza ampifandraisina ny kisoa mpiray tampopo (Inbreeding)", "Tahirizo ny boky fitantanana fiompiana")
                ),
                LivestockTopic(
                    id = "kisoa_trano",
                    title = "Trano Kisoa (Loge)",
                    subtitle = "Fanaovana loja sy fotodrafitrasa ara-pahasalamana",
                    details = "Loja ho an'ny kisoa vavy miteraka (Maternité): 2.5m x 2m misy barrière de protection ho an'ny kisokely. Loja fampitomboana: 1m² isaky ny kisoa 50kg.",
                    keyTips = listOf("Gorodona ciment misy pante 3% mba hivoahan'ny pissat", "Manga madio sy mangatsiatsiaka amin'ny fahavaratra", "Mangerie vato na ciment mora diovina")
                ),
                LivestockTopic(
                    id = "kisoa_sakafo",
                    title = "Sakafo Kisoa",
                    subtitle = "Provende feno sy fampiasana vokatra lokalina",
                    details = "Mais, bran de riz (farafotsy), tournesol, soja, manioc maina, poudre d'os, sy sira. Kisoa vavy mitondra vohoka: 2.5kg/andro. Kisoa vavy mampinono: 3kg + 0.5kg isaky ny kisokely.",
                    keyTips = listOf("Omeo rano madio 15 hatramin'ny 25 litatra isan'andro ny kisoa vavy mampinono", "Fenoina fer (Vy) ny kisokely amin'ny andro faha-3", "Fisarahana kisokely (Sèvrement) amin'ny 35-42 andro")
                ),
                LivestockTopic(
                    id = "kisoa_aretina",
                    title = "Peste Porcine (PPA) & Fisorohana",
                    subtitle = "Hiaro ny fiompiana amin'ny Pestes Porcine Africaine",
                    details = "Tsy misy vaksiny na fanafody ny Peste Porcine Africaine (PPA). Ny Biosecurité ihany no fiarovana: fefy manodidina, fanafody pamonoana mikraoba amin'ny fidirana (Pédiluve).",
                    keyTips = listOf("Aza mamahana restos de cuisine / fako an-drenivohitra amin'ny kisoa", "Sakanana tanteraka ny fidiran'ny mpividy kisoa ao anaty loja", "Vaksino amin'ny Rouget sy Charbon ny kisoa vavy")
                )
            )
        ),
        LivestockCategory(
            id = "bitro",
            name = "Fiompiana Bitro",
            description = "Fiompiana bitro ho an'ny hena mahatsara fahasalamana sy mora karakaraina",
            iconName = "CrueltyFree",
            topics = listOf(
                LivestockTopic(
                    id = "bitro_fomba",
                    title = "Fomba Fiompiana Bitro",
                    subtitle = "Fiompiana bitro matanjaka sy miteraka maro",
                    details = "Ny bitro vavy iray dia afaka miteraka in-5 hatramin'ny in-6 isan-taona (zanak'bitro 6-8 isaky ny miteraka). Mitondra vohoka mandritra ny 30-32 andro.",
                    keyTips = listOf("Apetraho ny boite à nids amin'ny andro faha-28 mitondra vohoka", "Aza kitihina amin'ny tanana tsy madio ny zanak'bitro vao teraka", "Atsaharo ny famelana bitro lahy sy vavy hiara-hipetraka foana")
                ),
                LivestockTopic(
                    id = "bitro_sakafo",
                    title = "Sakafo & Ahitra",
                    subtitle = "Fandaminana ahitra maina sy granule",
                    details = "Bitro dia mila ahitra feno cellulose (foin de légumineuses, Stylosanthes, Desmodium, ravin'omby) sy granules. Aza mampiasa ahitra lena misy rosée na misy poizina.",
                    keyTips = listOf("Ataovy maina (fanée) 4-6 ora amin'ny masoandro ny ahitra vao omena", "Rano madio amin'ny biberon na maugroire tsara hatrany", "Omeo ravin'akondro na mangahazo maina ho mineraly")
                )
            )
        ),
        LivestockCategory(
            id = "tantely",
            name = "Fiompiana Tantely",
            description = "Apiculture maoderina (Maha-ampona Langstroth na Kényane)",
            iconName = "Hive",
            topics = listOf(
                LivestockTopic(
                    id = "tantely_fomba",
                    title = "Fiompiana Tantely Maoderina",
                    subtitle = "Fampiasana ampona maoderina sy fiarovana valala",
                    details = "Ny ampona maoderina (Langstroth na Kényane) dia ahafahana mioty tantely madio sy mahazo hatramin'ny 15-25kg tantely isaky ny ampona isan-taona tsy mamono ny tantely.",
                    keyTips = listOf("Apetraho amin'ny toerana misy voninkazo sy rano manakaiky", "Aza mampiasa fanafody bibikely (insecticide) manodidina ny ampona", "Omeo siro de sucre amin'ny ririnina rehefa kely ny voninkazo")
                )
            )
        ),
        LivestockCategory(
            id = "trondro",
            name = "Fiompiana Trondro",
            description = "Pisciculture an-dranomamy (Tilapia, Carpe, Cyprin)",
            iconName = "SetMeal",
            topics = listOf(
                LivestockTopic(
                    id = "trondro_fomba",
                    title = "Fiompiana Trondro An-dranomamy",
                    subtitle = "Fikarakarana dobo sy fahana trondro",
                    details = "Mampiasa Tilapia Nilotica na Carpe Royale. Dobo misy fidirana sy hivoahan'ny rano tsara. Lalina: 1m hatramin'ny 1.2m. Density: trondro 3 hatramin'ny 5 isaky ny m².",
                    keyTips = listOf("Zezeho compost na taingim-biby ny dobo mba hisian'ny plancton (rano maitso)", "Omeo son de riz sy provende trondro (25-30% protéine) isan'andro", "Jereo ny taha oxygen amin'ny maraina")
                )
            )
        )
    )

    fun getCropsCategories(): List<CropCategory> = listOf(
        CropCategory(
            id = "vary",
            name = "Fambolena Vary",
            description = "Teknika SRI (Système de Riziculture Intensive) sy SRA nohatsaraina",
            iconName = "Grass",
            methods = "SRI (Système de Riziculture Intensive):\n1. Ketsa tanora 8-12 andro (manana ravina 2).\n2. Sarina ketsa tokana sady malalaka (25cm x 25cm na 30cm x 30cm).\n3. Tsy afahana rano mijanona foana: fampidiran-drano sy fanafoanana hafanana (Aération du sol).\n4. Fampiasana pysarba (sarcleuse rotative) in-3 hatramin'ny in-4 hanentanana ny fakan-dray.",
            soilPrep = "Labour lalina (20cm) 1-2 volana mialoha. Puddle tsara ny tanimbary hakatona ny vovo. Ampidiro ny compost 10 taonina/ha mandritra ny fandidiana tany.",
            fertilizing = "Zezika Compost organika 10t/ha + NPK (11-22-16) 150kg/ha amin'ny fambolena + Urée 100kg/ha zaraina in-2 (talohan'ny sarclage 1 sy fivoahan'ny salohim-bary).",
            careAndHarvest = "Sarclage ara-potoana. Fiarovana amin'ny borer na bibikely. Mijinja rehefa 80% amin'ny salohy no efa mavo volamena.",
            keyAdvice = listOf("Ny SRI dia mampitombo ny vokatra hatramin'ny 6 - 10 taonina/ha!", "Mitahiry rano 40% mihoatra ny fomba taloha", "Tsara ny ketsa tanora tokana ho an'ny fampitomboana zanaka")
        ),
        CropCategory(
            id = "legioma",
            name = "Fambolena Legioma",
            description = "Voatabia, Karoty, Anana, Poivron, Courgette sy Ananambo",
            iconName = "LocalGroceries",
            methods = "Fanaovana pepinière sy fambolena amin'ny planche nohatsaraina. Mampiasa paillage (brosses maina) mba hitahirizana la hamandoana sy hisorohana ny ahitra ratsy.",
            soilPrep = "Tany maivana, lonaka sy tsara draînage. Fanaovana boudin na planche avo 20cm amin'ny fahavaratra.",
            fertilizing = "Zezika compost organika masaka tsara (5kg/m²). Trofina purin d'ortie na ananambo isaky ny 10 andro.",
            careAndHarvest = "Tondrahy amin'ny maraina na hariva. Fiarovana amin'ny fanafody bio (fanalana parasy amin'ny savony mavo + trofina neemb/manga).",
            keyAdvice = listOf("Ataovy ny rotation des cultures (aza mamboly voatabia amin'ny tany nambolena ovy)", "Mampiasà paillage hampihenana rano", "Jinja amin'ny maraina mbola mangatsiaka")
        ),
        CropCategory(
            id = "voankazo",
            name = "Voankazo & Hazom-boankazo",
            description = "Papaye, Akondro, Mangahazo, Voasary, Mangamanga sy Zavoka",
            iconName = "Nutrition",
            methods = "Fandrihana lavaka lehibe (80cm x 80cm x 80cm). Fampiasana zana-kazo greffé ho an'ny vokatra haingana sy kalitao ambony.",
            soilPrep = "Saraho ny tany ambony (lonaka) sy tany ambany. Afangaroy amin'ny compost masaka 20kg sy poudres d'os ny tany ambony vao averina ao amin'ny lavaka.",
            fertilizing = "Zezika organika feno isan-taona amin'ny fanombohan'ny fahavaratra manodidina ny rantsan-kazo.",
            careAndHarvest = "Tadidio ny taille de formation sy fanadiovana ny rantsana maty na marary.",
            keyAdvice = listOf("Lavan-kazo greffé dia mamoa afaka 2-3 taona monja", "Arohoy amin'ny mpikiky sy parasy amin'ny fanafody bio", "Fadiana ny rano mijanona amin'ny fakan-kazo")
        ),
        CropCategory(
            id = "fambolena_maharitra",
            name = "Fambolena Maharitra (Agroécologie)",
            description = "Teknika fiarovana ny tany, Agroforesterie sy Association de cultures",
            iconName = "Eco",
            methods = "Fampifangaroana fambolena legumineuses (Stylosanthes, Crotalaria, Soja) mba hampiditra Azote ao amin'ny tany amin'ny fomba natiraly. Non-labour sy couverture permanente du sol.",
            soilPrep = "Aza dorana ny fako voly! Ampiasao ho couverture végétale hanatsarana ny humus.",
            fertilizing = "Biochar (sarabon'ny fako voly), compost, sy bokashi organika.",
            careAndHarvest = "Mitahiry ny zavaboary sy mikraoba mahasoa ao anaty tany.",
            keyAdvice = listOf("Miaraka mamboly Katsaka sy Tsaramaso: ny tsaramaso manome azote, ny katsaka no tohanany", "Hiaro ny tany amin'ny fozan-tany sy kaoseky", "Manatsara ny faharetan me any fambolena any aoriana")
        ),
        CropCategory(
            id = "zezika_organika",
            name = "Zezika Organika & Compost",
            description = "Fomba fanaovana Compost masaka tsara sy Zezika Kankana (Vermicompost)",
            iconName = "Compost",
            methods = "Laharana compost (Tas de compost 1.5m x 1.5m x 1.2m):\n- 1er couche: Rantsan-kazo sy mololo maina (Carbone).\n- 2ème couche: Bozaka maitso sy fako lakozia (Azote).\n- 3ème couche: Zezika omby/kisoa/akoho.\n- Tondrahy rano ampy sady avadika isaky ny 2-3 herinandro.",
            soilPrep = "Compost masaka tsara dia manana loko mainty fofona tany an'ala, tsy mafana intsony.",
            fertilizing = "Mampitombo ny humus sy fahaizan'ny tany mitahiry rano hatramin'ny 300%.",
            careAndHarvest = "Ataovy amin'ny toerana alokaloka tsy azon'ny orana mahery mibatana otrikaina.",
            keyAdvice = listOf("Ny compost masaka dia fanafody tsara indrindra ho an'ny tany Malagasy", "Aza ampidirina ny plastika na fako misy fanafody simika", "Purin d'ortie/ananambo: 1kg ravin'ananambo amin'ny 10L rano, avela 7 andro -> trofina 10%")
        ),
        CropCategory(
            id = "fikarakarana_tany",
            name = "Fikarakarana Tany & pH",
            description = "Fanatsarana ny tanimanga, tany fasika sy ady amin'ny acidité (Tany Maky)",
            iconName = "Landscape",
            methods = "Amboary ny tany misy acidité amin'ny alalan'ny fampidirana Chaux agricole (dolomie) na lavenona hazo (Cendre de bois).",
            soilPrep = "Bêchage sy aération tsy mamono ny mikraoba sy kankana (vers de terre).",
            fertilizing = "Lavenona hazo dia manankarena Potassium (K) sy Calcium (Ca).",
            careAndHarvest = "Ataovy ny courbe de niveau amin'ny tanety mba hisorohana ny riaka (érosion).",
            keyAdvice = listOf("Ny kankana tany no mpiasa tsara indrindra manao labour natiraly", "Aza avela nitanjaka amin'ny masoandro ny tany", "Ampiasao ny hazo fijanonan-driaka (Haie vive de Vétiver)")
        )
    )

    fun getEbooks(): List<EbookItem> = listOf(
        EbookItem(
            id = "book_akoho",
            title = "Torolalana Feno: Fiompiana Akoho Gasy Mahomby",
            category = "Fiompiana",
            author = "OIDE / Tantsaha Matihanina",
            pagesCount = 48,
            rating = 4.9f,
            summary = "Boky feno mampianatra ny fomba fiompiana akoho gasy nohatsaraina, fitaizana akohokely, fanaovana provende lokalina, ary kalandrie vaksiny feno.",
            tableOfContents = listOf(
                "Toko 1: Ny toetran'ny Akoho Gasy nohatsaraina",
                "Toko 2: Trano fiompiana sy fitaovana",
                "Toko 3: Fikajiana sakafo tsara indrindra",
                "Toko 4: Kalandrie Vaksiny sy Fitsaboana Aretina",
                "Toko 5: Fitantanana ara-bola sy tsena"
            ),
            fullContent = """
# TOROLALANA FENO: FIOMPIANA AKOHO GASY MAHOMBY

## TOKO 1: NY TOETRAN'NY AKOHO GASY NOHATSARAINA
Ny fiompiana akoho gasy nohatsaraina dia teknika fampifangaroana ny fahatanjahan'ny akoho gasy sy ny fahatsarana eo amin'ny fitantanana maoderina.
- Taham-pahavelomana: 90% (raha 30% amin'ny fiompiana nentim-paharazana).
- Atody isan-taona: 120 - 150 atody isaky ny akoho vavy.
- Lanja eo amin'ny 5 volana: 1.8 kg hatramin'ny 2.5 kg.

## TOKO 2: TRANO FIOMPIANA SY FITAOVANA
- Gorodona ambonin'ny tany (50cm hatramin'ny 1m).
- Valindrihana misy mololo na zozoro hampihena ny hafanana/mangatsiaka.
- Akany fanatodizana: akoho 4 isaky ny akany 1.
- Mangerie sy abreuvoir madio tsy azo dinganina.

## TOKO 3: FIKAJIANA SAKAFO
Formule Provende Akoho Gasy (100 kg):
1. Maïs totoy: 55 kg
2. Farafotsy (bran de riz): 15 kg
3. Tournesol na Soja: 20 kg
4. Poudre d'os / coquillage: 7 kg
5. Sira sy Premix Vitarave: 3 kg

## TOKO 4: KALANDRIE VAKSINY
- Day 7: HB1 (Barika / Newcastle) - dobo maso
- Day 14: Gumboro
- Day 21: LaSota (Rappel Newcastle)
- Day 45: Peste Aviaire / Variole
- Isaky ny 3 volana: Déparasitage amin'ny fanafody kankana.

## TOKO 5: FITANTANANA ARA-BOLA
Kajio foana ny vidin'ny sakafo isan'akoho.
Ny tsena tsara indrindra dia amin'ny fotoana fety (Krismasy, Paska, Asaramanitra, Repoblika).
            """.trimIndent()
        ),
        EbookItem(
            id = "book_sri",
            title = "Fambolena Vary SRI sy SRA eto Madagasikara",
            category = "Fambolena",
            author = "Mpanolotsaina Agri-Madagascar",
            pagesCount = 62,
            rating = 4.8f,
            summary = "Torolalana manokana momba ny Système de Riziculture Intensive (SRI) mba hahazoana vary 8 hatramin'ny 10 taonina isaky ny hektara.",
            tableOfContents = listOf(
                "Toko 1: Ny fitsipika 5 amin'ny SRI",
                "Toko 2: Fanaovana pepinière sy ketsa tanora",
                "Toko 3: Fikarakarana tanimbary sy sarclage",
                "Toko 4: Fampiasana Zezika Organika Compost",
                "Toko 5: Jinja sy fitehirizana vary"
            ),
            fullContent = """
# FAMBOLENA VARY SRI SY SRA ETO MADAGASIKARA

## TOKO 1: NY FITSIPIKA 5 AMIN'NY SRI
1. Ketsa tanora (8 hatramin'ny 12 andro): ketsa manana ravina 2 monja.
2. Ketsa tokana: sarina ketsa 1 isaky ny lavaka.
3. Elanelana malalaka: 25cm x 25cm na 30cm x 30cm miendrika kintana.
4. Fampidirana sy fanafoanana rano (Aération): tsy avela hisy rano mijanona foana.
5. Sarclage rotatif in-3 hatramin'ny in-4 miaraka amin'ny compost.

## TOKO 2: PEPINIÈRE SRI
- Pepinière maina na semi-aquatique.
- Zezeho compost masaka tsara 5kg/m².
- Loahy kely ny ketsa mba tsy ho tapaka ny fakan-dray mandritra ny fanaingana.

## TOKO 3: SARCLAGE SY AIRATION
Ny sarcleuse rotative dia manapaka ny ahitra ratsy sady mampiditra oxygen ao amin'ny fakan-dray. Izany no mampitombo ny zanaka vary hatramin'ny 50 - 80 salohy isaky me fototra!
            """.trimIndent()
        ),
        EbookItem(
            id = "book_kisoa",
            title = "Boky Kisoa: Fomba Fiompiana sy Fitsaboana",
            category = "Fiompiana",
            author = "Dr. Veterinera Randria",
            pagesCount = 54,
            rating = 4.9f,
            summary = "Boky feno momba ny fiompiana kisoa vavy miteraka, fampitomboana kisokely, fikajiana provende kisoa sy fiarovana amin'ny PPA.",
            tableOfContents = listOf(
                "Toko 1: Safidy kisoa fiompiana",
                "Toko 2: Trano kisoa sy Maternité",
                "Toko 3: Sakafo feno araka ny dingana",
                "Toko 4: Peste Porcine (PPA) sy Biosecurité",
                "Toko 5: Kajy tombony kisoa 10"
            ),
            fullContent = """
# BOKY KISOA: FOMBA FIOMPIANA SY FITSABOANA

## TOKO 1: SAFIDY KISOA
- Karazana Large White: tsara ho an'ny fiterahana sy ronono.
- Karazana Pietrain / Landrace: tsara ho an'ny hena madio.
- Kisoa vavy manana nono 12-14 tsara fitaovana.

## TOKO 2: TRANO MATERNITÉ
- Loja misy barrière fiarovana ny kisokely mba tsy ho voatosiky ny reny.
- Rano madio tsy tapaka (20 Litra/andro ho an'ny vavy mampinono).
- Fanasana kisokely fer (Vy) amin'ny andro faha-3.

## TOKO 3: PESTE PORCINE (PPA)
- Tsy misy fanafody na vaksiny.
- Fefy biosecurité: sarina olona avy ivelany.
- Pédiluve misy fanafody disinsectant eo amin'ny fidirana rehetra.
            """.trimIndent()
        ),
        EbookItem(
            id = "book_agro",
            title = "Agroécologie sy Zezika Organika Compost",
            category = "Fambolena",
            author = "GSDM Madagascar",
            pagesCount = 40,
            rating = 4.7f,
            summary = "Boky mampianatra fanaovana compost organika, purin natiraly fiarovana amin'ny parasy, sy fiarovana ny tany amin'ny fiovan'ny toetrandro.",
            tableOfContents = listOf(
                "Toko 1: Fanaovana Compost masaka",
                "Toko 2: Zezika Kankana (Vermicompost)",
                "Toko 3: Purin Ananambo sy Ortive",
                "Toko 4: Couverture végétale sy Non-labour"
            ),
            fullContent = """
# AGROÉCOLOGIE SY ZEZIKA ORGANIKA COMPOST

## TOKO 1: FANAOVANA COMPOST
Ny compost dia manome aina ny tany.
Inona no ilaina?
- 40% Maina (Manioc, mololo, rantsan-kazo) -> Carbone
- 40% Maitso (Bozaka, fako lakozia, anana) -> Azote
- 20% Zezika biby (Omby, kisoa, akoho) -> Mikraoba

Aina sy rano: tondrahy fitaovana hatrany, avadika isaky ny 15 andro. Afaka 60-90 andro dia masaka tsara.
            """.trimIndent()
        )
    )

    fun getNewsAndTips(): List<NewsItem> = listOf(
        NewsItem(
            id = "news_1",
            title = "Torohevitra: Fiarovana ny Akoho amin'ny Areti-mifindra amin'ny Ririnina",
            category = "Torohevitra",
            date = "28 Jolay 2026",
            summary = "Amin'izao ririnina izao, mitombo ny risika amin'ny Barika sy ny sery amin'ny akoho. Indreto ny fepetra 3 tokony horaisina hanoherana izany.",
            fullText = "1. Sakanana ny rivotra mangatsiaka mifofofofo ao anaty trano akoho amin'ny alalan'ny lamba na gony amin'ny alina.\n2. Omeo otrikaina Vitamine C sy aziditina amin'ny rano hosotroiny (fitaovana fanafody natiraly toy ny sakamalaho sy voasary makirana).\n3. Vaksino feno ara-potoana amin'ny HB1 sy LaSota.",
            isFeatured = true
        ),
        NewsItem(
            id = "news_2",
            title = "Ohabolana Tantsaha sy Fahendrena Malagasy",
            category = "Fahendrena",
            date = "28 Jolay 2026",
            summary = "\"Aza ny fahatelon'ny ketsa no jerena, fa ny fahatomomban'ny salohy.\" - Ny faharetana sy ny fikarakarana ara-dalàna no antoky ny vokatra maro.",
            fullText = "Ny fiompiana sy fambolena dia mila faharetana, fahalalana maoderina sy fitiavana ny asa. Rehefa manaraka ny teknika nohatsaraina ny tantsaha dia azo antoka ny fahombiazana sy ny fahavitan-tena ara-tsakafo.",
            isFeatured = false
        ),
        NewsItem(
            id = "news_3",
            title = "Vidim-bokatra eo amin'ny Tsena Lehibe Malagasy",
            category = "Tsena",
            date = "28 Jolay 2026",
            summary = "Vary gasy: 3,200 Ar/kg | Akoho gasy lehibe: 28,000 Ar | Kisoa hena: 18,000 Ar/kg | Voatabia: 2,500 Ar/kg.",
            fullText = "Misy fisondrotana kely ny vidin'ny akoho gasy amin'izao fotoana izao vokatry ny fitomboan'ny tinady an-drenivohitra. Fotoana tsara ho an'ny mpiompy hamoahana ny vokatra.",
            isFeatured = false
        )
    )

    fun getWeatherCities(): List<WeatherCity> = listOf(
        WeatherCity(
            name = "Antananarivo",
            region = "Analamanga",
            tempC = 19,
            condition = "Misy rahona kely sy masoandro",
            humidity = 68,
            rainProb = 15,
            windKmh = 12,
            farmingAdvice = "Toetrandro tsara amin'ny fikarakarana tany legioma sy sarclage tanimbary. Halaviro ny fampiasana fanafody amin'ny masoandro mahery."
        ),
        WeatherCity(
            name = "Antsirabe",
            region = "Vakinankaratra",
            tempC = 15,
            condition = "Mangatsiaka sy maimbo sy fofon-dranomandry",
            humidity = 78,
            rainProb = 20,
            windKmh = 14,
            farmingAdvice = "Arohoy amin'ny mangatsiaka ny akohokely sy kisokely vao teraka. Ampiasao ny poêle na jiro fanafanana amin'ny alina."
        ),
        WeatherCity(
            name = "Mahajanga",
            region = "Boeny",
            tempC = 31,
            condition = "Masoandro mahery sy mafana",
            humidity = 55,
            rainProb = 5,
            windKmh = 18,
            farmingAdvice = "Tondrahy tsara amin'ny maraina vao maraina na hariva ny legioma. Omeo rano madio sy mangatsiatsiaka ampy ny biby fiompy."
        ),
        WeatherCity(
            name = "Toamasina",
            region = "Atsinanana",
            tempC = 25,
            condition = "Oram-baratra sy mavesatra",
            humidity = 85,
            rainProb = 65,
            windKmh = 22,
            farmingAdvice = "Karakarao ny saosahany sy fivoahan'ny rano amin'ny tanimboly mba hisorohana ny rano mijanona mampatonga fotorana fakan-kazo."
        ),
        WeatherCity(
            name = "Fianarantsoa",
            region = "Matsiatra Ambony",
            tempC = 18,
            condition = "Mazy sy zavona maraina",
            humidity = 75,
            rainProb = 25,
            windKmh = 10,
            farmingAdvice = "Atsaharo ny fanaovana ketsa mandritra ny zavona mavesatra. Fotoana tsara ho an'ny fambolena hazo sy agroforesterie."
        ),
        WeatherCity(
            name = "Toliara",
            region = "Atsimo Andrefana",
            tempC = 28,
            condition = "Maina sy masoandro",
            humidity = 48,
            rainProb = 0,
            windKmh = 20,
            farmingAdvice = "Mampiasà paillage (mulch) matevina amin'ny fambolena rehetra hitahirizana ny hamandoan'ny tany."
        ),
        WeatherCity(
            name = "Sambava",
            region = "Sava",
            tempC = 26,
            condition = "Misy orana kely tsiroara",
            humidity = 80,
            rainProb = 40,
            windKmh = 15,
            farmingAdvice = "Toetrandro mifanaraka tsara amin'ny fambolena vanille, kafe sy voankazo tany mafana."
        ),
        WeatherCity(
            name = "Ambatondrazaka",
            region = "Alaotra Mangoro",
            tempC = 22,
            condition = "Masoandro tsara sy rivotra moramora",
            humidity = 65,
            rainProb = 10,
            windKmh = 12,
            farmingAdvice = "Toetrandro mety tsara amin'ny fikarakarana sy jinjana tanimbary Alaotra."
        )
    )

    fun getVaccinesByAnimal(): List<VaccineInfo> = listOf(
        VaccineInfo(
            id = "v_1",
            animalType = "Akoho",
            vaccineName = "HB1 (Newcastle / Barika)",
            targetDisease = "Aretina Newcastle (Barika)",
            agePeriod = "Andro faha-7 (7 jours)",
            method = "Dobo maso na vava (Goutte oculaire)",
            frequency = "In-1 amin'ny fahazazana",
            notes = "Fiaraovana voalohany hanoherana ny Barika. Tahirizo amin'ny mangatsiaka."
        ),
        VaccineInfo(
            id = "v_2",
            animalType = "Akoho",
            vaccineName = "Gumboro (IBD)",
            targetDisease = "Aretina Gumboro (Maladie de Gumboro)",
            agePeriod = "Andro faha-14 (14 jours)",
            method = "Rano hosotroina (Eau de boisson)",
            frequency = "In-1",
            notes = "Hanoherana ny fahasimban'ny fiarovan-tena ao anatin'ny akoho."
        ),
        VaccineInfo(
            id = "v_3",
            animalType = "Akoho",
            vaccineName = "LaSota (Rappel Newcastle)",
            targetDisease = "Barika (Newcastle)",
            agePeriod = "Andro faha-21 sy isaky ny 3 volana",
            method = "Rano hosotroina na trofina",
            frequency = "Isaky ny 3 volana",
            notes = "Mamerina ny hery fiarovana amin'ny barika ho an'ny akoho lehibe."
        ),
        VaccineInfo(
            id = "v_4",
            animalType = "Kisoa",
            vaccineName = "Rouget du Porc",
            targetDisease = "Aretina Rouget (Tachage rouge)",
            agePeriod = "Afaka 2 volana ary isan-taona",
            method = "Tsindrona amin me hozatra (Intramusculaire)",
            frequency = "Isan-taona (1 fois/an)",
            notes = "Fiarovana ny kisoa vavy miteraka sy kisoa mampavitrika."
        ),
        VaccineInfo(
            id = "v_5",
            animalType = "Kisoa",
            vaccineName = "Charbon Symptomatique",
            targetDisease = "Charbon porcin",
            agePeriod = "3 volana",
            method = "Tsindrona (Injection)",
            frequency = "Isan-taona",
            notes = "Atolotry ny Dokotera Veterinera manam-pahaizana."
        ),
        VaccineInfo(
            id = "v_6",
            animalType = "Omby",
            vaccineName = "Charbon Bactéridien sy Symptomatique",
            targetDisease = "Aretina Charbon Omby",
            agePeriod = "Afaka 6 volana",
            method = "Tsindrona Sous-cutanée",
            frequency = "Isan-taona talohan'ny fahavaratra",
            notes = "Tena ilaina amin'ny omby fiompiana sy omby tosika eto Madagasikara."
        )
    )

    fun getMarketplaceItems(): List<MarketplaceItem> = listOf(
        MarketplaceItem(
            id = "m_1",
            title = "Akoho Gasy Mpanatodizana Nohatsaraina",
            category = "Akoho & Vorona",
            priceAr = 28000,
            commissionPercent = 5.0,
            sellerName = "Rakoto Jean (Fermeur Analamanga)",
            sellerPhone = "034 12 345 67",
            location = "Antananarivo (Ivato)",
            quantityAvailable = "50 akoho",
            description = "Akoho gasy nohatsaraina 6 volana, efa manatody tsara, vaksina feno HB1 sy LaSota. Salama tsara."
        ),
        MarketplaceItem(
            id = "m_2",
            title = "Kisoa Vavy Large White Mitondra Vohoka",
            category = "Kisoa & Omby",
            priceAr = 650000,
            commissionPercent = 5.0,
            sellerName = "Pauline Rasoa",
            sellerPhone = "032 88 990 00",
            location = "Antsirabe (Betafo)",
            quantityAvailable = "2 kisoa vavy",
            description = "Kisoa Large White vavy faha-2 miteraka, mitondra vohoka 2 volana. Nono 14, efa vaksina Rouget."
        ),
        MarketplaceItem(
            id = "m_3",
            title = "Vary Makalioka SRI Gony 50kg",
            category = "Vary & Legioma",
            priceAr = 160000,
            commissionPercent = 5.0,
            sellerName = "Coopérative Alaotra",
            sellerPhone = "033 11 223 34",
            location = "Ambatondrazaka",
            quantityAvailable = "100 gony",
            description = "Vary Makalioka tsara kalitao jinjana vao haingana amin'ny teknika SRI. Tsy misy vato na mololo."
        ),
        MarketplaceItem(
            id = "m_4",
            title = "Zezika Organika Compost Masaka Gony 25kg",
            category = "Zezika & Fitaovana",
            priceAr = 22000,
            commissionPercent = 5.0,
            sellerName = "EcoCompost Mada",
            sellerPhone = "034 55 667 78",
            location = "Antananarivo (Ampasampito)",
            quantityAvailable = "200 gony",
            description = "Compost masaka tsara (100% organika), manankarena azote sy humus ho an'ny legioma sy fambolena vary."
        ),
        MarketplaceItem(
            id = "m_5",
            title = "Vaksiny HB1 Barika Akoho (100 doses)",
            category = "Fanafody",
            priceAr = 15000,
            commissionPercent = 5.0,
            sellerName = "Pharmacie Vétérinaire Tsara",
            sellerPhone = "034 99 887 76",
            location = "Mahajanga",
            quantityAvailable = "30 flacons",
            description = "Vaksiny HB1 vaovao voatahiry amin'ny chaînes de froid (2°C-8°C) ho an'ny akohokely 7 andro."
        ),
        MarketplaceItem(
            id = "m_6",
            title = "Kisokely Landrace Pur 2 Volana",
            category = "Kisoa & Omby",
            priceAr = 120000,
            commissionPercent = 5.0,
            sellerName = "Fiompiana Randria",
            sellerPhone = "032 44 556 67",
            location = "Fianarantsoa",
            quantityAvailable = "12 kisokely",
            description = "Kisokely 60 andro masaka sèvrement, efa nahazo vy Fer sy déparasitage. Matanjaka tsara."
        ),
        MarketplaceItem(
            id = "m_7",
            title = "Omby Tosika Paire (Omby Dra) 4 Taona",
            category = "Kisoa & Omby",
            priceAr = 3200000,
            commissionPercent = 5.0,
            sellerName = "Dadafara Toliara",
            sellerPhone = "034 77 665 54",
            location = "Toliara",
            quantityAvailable = "1 paire",
            description = "Omby tosika roa matanjaka zatra miasa tanimbary sy charrette. Salama tsara, vaksina charbon."
        )
    )

    fun getVideoPosts(): List<VideoPost> = listOf(
        VideoPost(
            id = "v_1",
            authorName = "Fermeur Jean (Mpiompy Akoho)",
            authorRole = UserRole.TANTSAHA,
            authorPhone = "034 12 345 67",
            location = "Ivato, Antananarivo",
            title = "🎥 Jereo ny fahatsaran'ireto akoho gasy nohatsaraina ampiasaina ho mpanatodizana!",
            description = "Filming mivantana avy ao amin meuble fiompiana. Akoho 60 efa hovidiana, manatody anio. Antsoy mivantana raha mila grossiste na déteil.",
            mediaType = "VIDEO",
            videoDurationText = "1:20",
            priceAr = 28000,
            likesCount = 245,
            commentsCount = 38,
            sharesCount = 19,
            datePosted = "2 adiny lasa",
            category = "Akoho & Vorona"
        ),
        VideoPost(
            id = "p_1",
            authorName = "Rakoto Jean (Tantsaha)",
            authorRole = UserRole.TANTSAHA,
            authorPhone = "034 12 345 67",
            location = "Ivato, Antananarivo",
            title = "📸 Sary: Kisoa miteraka 12 salama tsara vao nateraka tamin'ity maraina ity",
            description = "Ireo kisoa kely vao teraka tamin'ny 5 ora maraina. Providence matanjaka, efa ampy reny sy mihinana matetika. Misy alaina afaka 1 volana.",
            mediaType = "PHOTO",
            photoTag = "🐖 KISOA VOKATRA (SARY)",
            priceAr = 120000,
            likesCount = 310,
            commentsCount = 42,
            sharesCount = 25,
            datePosted = "3 adiny lasa",
            category = "Kisoa & Omby"
        ),
        VideoPost(
            id = "v_2",
            authorName = "Coopérative Alaotra (Grossiste Vary)",
            authorRole = UserRole.GROSSISTE,
            authorPhone = "033 11 223 34",
            location = "Ambatondrazaka",
            title = "📹 Camion 10 tonnes Vary Makalioka vao tonga ao amin'ny dépôt gros!",
            description = "Tonga anio ny jinjana vary makalioka SRI vaovao. Manolotra prix grossiste ho an'ny mpivarotra madinika amin'ny gony 50kg sy kamiao.",
            mediaType = "VIDEO",
            videoDurationText = "0:55",
            priceAr = 160000,
            likesCount = 512,
            commentsCount = 84,
            sharesCount = 62,
            datePosted = "5 adiny lasa",
            category = "Vary & Legioma"
        ),
        VideoPost(
            id = "p_2",
            authorName = "Pauline Rasoa (Mpivarotra Madinika)",
            authorRole = UserRole.MPIVAROTRA_MADINIKA,
            authorPhone = "032 88 990 00",
            location = "Anosibe Market, Antananarivo",
            title = "📸 Sary tsena: Voatabia sy Karoty vaovao vao tonga avy any Vakinankaratra",
            description = "Stock vaovao ho an'ny mpivarotra sy mpanjifa madinika. Voatabia be hena sy karoty mavo tsara, gony sy kagoy hovidina.",
            mediaType = "PHOTO",
            photoTag = "🥕 LEGIOMA TSENA (SARY)",
            priceAr = 3500,
            likesCount = 142,
            commentsCount = 18,
            sharesCount = 8,
            datePosted = "8 adiny lasa",
            category = "Vary & Legioma"
        )
    )

    fun getCommunityGroups(): List<CommunityGroup> = listOf(
        CommunityGroup(
            id = "g_1",
            name = "🐔 Fiaraha-miasa Mpiompy Akoho Gasy & Nohatsaraina",
            roleFocus = "Tantsaha & Grossiste",
            membersCount = 1420,
            description = "Vondrona hifanakalozana torohevitra, sakafo akoho, vaksiny ary hitadiavana mpambongadiny am-mora.",
            recentPost = "Randria: Misy vaksiny HB1 sy LaSota ve eny Ivato anio?"
        ),
        CommunityGroup(
            id = "g_2",
            name = "🌾 Vondrona Grossiste & Mpambongadiny Vary Madagascar",
            roleFocus = "Grossiste & Mpivarotra Madinika",
            membersCount = 890,
            description = "Vondrona manokana ho an'ny mpanangona, grossiste vary, katsaka ary voanjo manerana ny Nosy.",
            recentPost = "Coop Alaotra: Vary Makalioka gony 50kg vao tonga 500 bags!"
        ),
        CommunityGroup(
            id = "g_3",
            name = "🐖 Resaka Fiompiana Kisoa & Varotra Hena",
            roleFocus = "Mpiompy & Mpivarotra Madinika",
            membersCount = 2150,
            description = "Teknika fiompiana kisoa Large White, Landrace, sakafo provende ary fitadiavana mpividy kisoa vavy/miteraka.",
            recentPost = "Pauline: Peste Porcine sy vaksina fiarovana."
        )
    )

    fun getCommunityMessages(): List<CommunityMessage> = listOf(
        CommunityMessage(
            id = "cm_1",
            senderName = "Coopérative Alaotra",
            senderRole = UserRole.GROSSISTE,
            text = "Salama Jean! Hitako ilay akoho gasy nohatsaraina napetrakao tamin'ny video. Mbola misy 30 akoho ve azonay vidina gros?",
            timeAgo = "10 min lasa",
            isMe = false
        ),
        CommunityMessage(
            id = "cm_2",
            senderName = "Moi (Rakoto Jean)",
            senderRole = UserRole.TANTSAHA,
            text = "Salama tompoko! Eny mbola misy 30 akoho manatody tsara. Mahazo fihenam-bidy 5% ianao satria mividy am-bonjy gros.",
            timeAgo = "5 min lasa",
            isMe = true
        )
    )
}

