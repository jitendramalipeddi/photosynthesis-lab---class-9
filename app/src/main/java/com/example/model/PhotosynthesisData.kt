package com.example.model

object PhotosynthesisData {

    val readingSections = listOf(
        ReadingSection(
            id = "sec_1_intro",
            title = "1. What is Photosynthesis?",
            subtitle = "The Energy Transformer of Life on Earth",
            keyConcept = "Photosynthesis converts light energy into chemical energy stored as glucose.",
            contentMarkdown = "Photosynthesis is an autotrophic process where green plants, algae, and cyanobacteria synthesize organic food (glucose) from inorganic raw materials (carbon dioxide and water) using solar light energy absorbed by chlorophyll.",
            bulletPoints = listOf(
                "Word Meaning: Photo = Light, Synthesis = Putting together.",
                "Chemical Equation: 6CO₂ + 6H₂O + Sunlight → C₆H₁₂O₆ + 6O₂",
                "Byproduct: Oxygen (O₂) released into atmosphere essential for aerobic respiration.",
                "Primary Organ: Plant Leaves (specifically mesophyll cells rich in chloroplasts)."
            ),
            videoUrl = "https://www.youtube.com/watch?v=sQK3Yr4Sc_U",
            videoTitle = "Class 9 Biology: Introduction to Photosynthesis & Leaf Structure",
            mediaType = "ANIMATION"
        ),
        ReadingSection(
            id = "sec_2_chloroplast",
            title = "2. Chloroplast & Leaf Structure",
            subtitle = "Inside the Photosynthetic Factory",
            keyConcept = "Chloroplasts contain thylakoid membranes (site of light reaction) and stroma (site of dark reaction).",
            contentMarkdown = "Leaves have special structures designed for efficient gas exchange and light absorption. Stomata (singular: stoma) are tiny microscopic pores surrounded by guard cells that open and close to regulate CO₂ intake and H₂O transpiration.",
            bulletPoints = listOf(
                "Double Membrane: Outer and inner membranes protect the internal matrix.",
                "Thylakoids: Flattened disc-like sacs arranged in stacks called Grana (singular: Granum).",
                "Chlorophyll: Pigment embedded in thylakoid membranes that captures blue & red light.",
                "Stroma: Fluid-filled matrix containing enzymes for the Calvin cycle."
            ),
            videoUrl = "https://www.youtube.com/watch?v=cm_01uOvhX8",
            videoTitle = "Chloroplast Anatomy & Stomata Function Explained",
            mediaType = "DIAGRAM"
        ),
        ReadingSection(
            id = "sec_3_light_reaction",
            title = "3. Light-Dependent Phase (Photochemical)",
            subtitle = "Trapping Solar Energy & Photolysis of Water",
            keyConcept = "Light reaction takes place in Thylakoids and splits H₂O into H⁺, electrons, and O₂ gas.",
            contentMarkdown = "This phase requires direct sunlight. Sunlight strikes chlorophyll molecules, exciting electrons. The energy is used to split water molecules (photolysis) and generate high-energy chemical carriers ATP and NADPH.",
            bulletPoints = listOf(
                "Location: Thylakoid Membrane / Grana.",
                "Photolysis of Water: 2H₂O → 4H⁺ + 4e⁻ + O₂↑",
                "Energy Compounds Formed: ATP (Adenosine Triphosphate) & NADPH.",
                "Oxygen Release: Oxygen gas diffuses out of the leaf through stomata."
            ),
            videoUrl = "https://www.youtube.com/watch?v=WMfWXVEisjw",
            videoTitle = "Light Reactions & Photolysis Step-by-Step Animation",
            mediaType = "VIDEO"
        ),
        ReadingSection(
            id = "sec_4_dark_reaction",
            title = "4. Light-Independent Phase (Calvin Cycle)",
            subtitle = "Fixing Carbon Dioxide into Sugar",
            keyConcept = "Dark reaction occurs in the Stroma and converts CO₂ into Glucose using ATP & NADPH.",
            contentMarkdown = "Also known as the Dark Reaction or Biosynthetic Phase, this reaction does not require direct light, but depends on ATP and NADPH produced during the light reaction. RuBisCO enzyme fixes carbon dioxide into 3-carbon sugars.",
            bulletPoints = listOf(
                "Location: Chloroplast Stroma.",
                "Key Enzyme: RuBisCO (Ribulose-1,5-bisphosphate carboxylase-oxygenase).",
                "Output: Glucose (C₆H₁₂O₆), stored as starch in plant tissues.",
                "Regeneration: ADP and NADP⁺ return to light reactions to repeat the cycle."
            ),
            videoUrl = "https://www.youtube.com/watch?v=0UzMaoaXKaM",
            videoTitle = "Calvin Cycle & Carbon Fixation Explained for Class 9",
            mediaType = "ANIMATION"
        ),
        ReadingSection(
            id = "sec_5_factors",
            title = "5. Factors Affecting Photosynthesis",
            subtitle = "Environmental Drivers & Rate Limits",
            keyConcept = "Light intensity, CO₂ concentration, temperature, and water availability govern photosynthetic rate.",
            contentMarkdown = "According to Blackman's Law of Limiting Factors, the rate of photosynthesis is limited by the factor that is nearest to its minimum value.",
            bulletPoints = listOf(
                "Light Intensity: Increases rate up to a saturation point.",
                "CO₂ Concentration: Atmosphere has ~0.04% CO₂; higher levels increase rate up to saturation.",
                "Temperature: Optimum range is 25°C - 35°C; extreme heat denatures enzymes.",
                "Water Availability: Water scarcity causes stomata to close, restricting CO₂ intake."
            ),
            videoUrl = "https://www.youtube.com/watch?v=rAJ3weS2B7g",
            videoTitle = "Experiments on Factors Affecting Photosynthesis Rate",
            mediaType = "DIAGRAM"
        )
    )

    val vocabularyTerms = listOf(
        VocabularyTerm("v1", "Chlorophyll", "klōr′-ə-fil", "Green photosynthetic pigment in chloroplast thylakoids that absorbs light energy.", "Leaves turn yellow when chlorophyll degrades in autumn."),
        VocabularyTerm("v2", "Chloroplast", "klōr′-ə-plast", "Double-membraned organelle in plant cells where photosynthesis occurs.", "Mesophyll cells contain 20 to 100 chloroplasts."),
        VocabularyTerm("v3", "Stomata", "stō′-mə-tə", "Microscopic pores on leaf epidermis bounded by guard cells for gas exchange.", "Stomata open during daytime to absorb CO₂."),
        VocabularyTerm("v4", "Thylakoid", "thī′-lə-koid", "Membrane-bound disc inside chloroplast where light reactions occur.", "Stacks of thylakoids form grana."),
        VocabularyTerm("v5", "Photolysis", "fō-tol′-ə-sis", "Light-driven breakdown of water molecules into hydrogen ions and oxygen.", "Photolysis produces the oxygen gas we breathe."),
        VocabularyTerm("v6", "Calvin Cycle", "kal′-vin sī-kəl", "Series of chemical reactions in stroma that converts CO₂ into glucose.", "Named after Nobel laureate Melvin Calvin."),
        VocabularyTerm("v7", "Guard Cells", "gärd selz", "Pair of specialized epidermal cells controlling the opening/closing of a stoma.", "Guard cells swell with water to open stomata.")
    )

    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            type = QuestionType.MULTIPLE_CHOICE,
            questionText = "1. Which organelle is known as the site of photosynthesis in plant cells?",
            options = listOf("Mitochondria", "Chloroplast", "Ribosome", "Endoplasmic Reticulum"),
            correctAnswer = "Chloroplast",
            explanation = "Chloroplasts contain chlorophyll and the metabolic machinery required for both light and dark reactions of photosynthesis.",
            hint = "Look for the double-membraned green plastid organelle."
        ),
        QuizQuestion(
            id = 2,
            type = QuestionType.MULTIPLE_CHOICE,
            questionText = "2. What is the primary green pigment that absorbs solar energy during photosynthesis?",
            options = listOf("Carotene", "Xanthophyll", "Chlorophyll", "Hemoglobin"),
            correctAnswer = "Chlorophyll",
            explanation = "Chlorophyll (specifically Chlorophyll-a and Chlorophyll-b) absorbs light primarily in the blue and red spectrums and reflects green light.",
            hint = "It gives leaves their green color."
        ),
        QuizQuestion(
            id = 3,
            type = QuestionType.MULTIPLE_CHOICE,
            questionText = "3. In which exact region of the chloroplast does the Light-Dependent reaction take place?",
            options = listOf("Stroma", "Thylakoid Membrane", "Outer Membrane", "Cell Wall"),
            correctAnswer = "Thylakoid Membrane",
            explanation = "The light-dependent reaction occurs in the thylakoid membranes (grana) where chlorophyll pigments and electron transport chains are located.",
            hint = "It happens in the disc-like membrane stacks called grana."
        ),
        QuizQuestion(
            id = 4,
            type = QuestionType.WRITTEN_SINGLE_WORD,
            questionText = "4. What gas is released into the atmosphere as a byproduct during the photolysis of water in photosynthesis?",
            correctAnswer = "Oxygen",
            explanation = "Photolysis (splitting of water: 2H₂O → 4H⁺ + 4e⁻ + O₂) releases Oxygen gas (O₂) as a essential byproduct.",
            hint = "The vital gas humans and animals inhale for respiration (e.g. Oxygen or O2)."
        ),
        QuizQuestion(
            id = 5,
            type = QuestionType.WRITTEN_SINGLE_WORD,
            questionText = "5. What tiny microscopic pores on the surface of leaves regulate gas exchange (CO₂ intake and O₂ release)?",
            correctAnswer = "Stomata",
            explanation = "Stomata (plural of stoma) are pores on leaf surfaces flanked by guard cells that control gas exchange and transpiration.",
            hint = "Plural term for leaf pores flanked by guard cells."
        )
    )
}
