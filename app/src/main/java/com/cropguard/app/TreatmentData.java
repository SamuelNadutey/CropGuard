package com.cropguard.app;

import java.util.HashMap;
import java.util.Map;

public class TreatmentData {

    public static class Info {
        public final String displayName;
        public final String whatItIs;
        public final String[] whatToDo;
        public final String treatment;
        public final String severity; // "healthy", "warn", or "alert"
        public final String authority;

        public Info(String displayName, String whatItIs, String[] whatToDo,
                    String treatment, String severity, String authority) {
            this.displayName = displayName;
            this.whatItIs = whatItIs;
            this.whatToDo = whatToDo;
            this.treatment = treatment;
            this.severity = severity;
            this.authority = authority;
        }
    }

    private static final Map<String, Info> MAP = new HashMap<>();

    static {
        // ---- CASHEW (COCOBOD / CRIG) ----
        MAP.put("cashew_anthracnose", new Info("Cashew Anthracnose",
                "A fungal disease (Colletotrichum) causing dark lesions on leaves, flowers and young nuts, reducing yield.",
                new String[]{"Prune and destroy infected twigs, flowers and mummified nuts.",
                        "Improve airflow in the canopy; avoid overcrowding.",
                        "Keep the orchard floor clear of fallen infected material."},
                "A copper-based or mancozeb fungicide is reported effective in Ghana, applied around flowering. Confirm the exact product and rate with a COCOBOD/CRIG extension officer.",
                "alert", "COCOBOD / CRIG"));

        MAP.put("cashew_gumosis", new Info("Cashew Gummosis",
                "A dieback disease (Lasiodiplodia) causing gum oozing from stems and twigs, with blight and branch death.",
                new String[]{"Cut out and burn gumming or dying twigs well below the lesion.",
                        "Protect trees from bark wounds, which are entry points.",
                        "Maintain tree vigour with good nutrition."},
                "Apply a protectant/wound dressing on cut surfaces; a copper-based fungicide may help. Surgical removal is the main control. Confirm with a COCOBOD/CRIG officer.",
                "alert", "COCOBOD / CRIG"));

        MAP.put("cashew_healthy", new Info("Healthy Cashew",
                "No disease detected. The leaf appears healthy.",
                new String[]{"Keep scouting the orchard regularly.",
                        "Maintain spacing and pruning for airflow.",
                        "Continue good sanitation and balanced nutrition."},
                "No treatment needed. Re-scan if symptoms appear.",
                "healthy", "COCOBOD / CRIG"));

        MAP.put("cashew_leaf_miner", new Info("Cashew Leaf Miner",
                "An insect whose larvae tunnel inside leaves, leaving mined trails and weakening new flush.",
                new String[]{"Remove and destroy heavily mined leaves.",
                        "Monitor new flush closely — the most vulnerable stage.",
                        "Encourage natural enemies; avoid unnecessary spraying."},
                "If infestation is heavy on new flush, a targeted, reduced-impact insecticide may be used. Confirm the product with a COCOBOD/CRIG officer.",
                "warn", "COCOBOD / CRIG"));

        MAP.put("cashew_red_rust", new Info("Cashew Red Rust",
                "A parasitic alga (Cephaleuros) causing orange-red velvety patches on leaves, common in shaded, crowded trees.",
                new String[]{"Prune to improve airflow and reduce shade.",
                        "Reduce prolonged leaf wetness.",
                        "Practise orchard sanitation."},
                "A copper-based fungicide is the standard approach where treatment is warranted. Confirm with a COCOBOD/CRIG officer.",
                "warn", "COCOBOD / CRIG"));

        // ---- CASSAVA (MOFA / PPRSD) ----
        MAP.put("cassava_bacterial_blight", new Info("Cassava Bacterial Blight",
                "A bacterial disease (Xanthomonas) causing angular leaf spots, wilting and dieback; spread by cuttings and tools.",
                new String[]{"Use clean, disease-free planting material.",
                        "Plant resistant/tolerant varieties.",
                        "Disinfect cutting tools; remove and destroy infected plants.",
                        "Intercrop with maize to reduce spread."},
                "No routine chemical cure — control is cultural and varietal. Confirm variety choice with a MOFA/PPRSD extension officer.",
                "alert", "MOFA / PPRSD"));

        MAP.put("cassava_brown_spot", new Info("Cassava Brown Spot",
                "A usually minor fungal leaf spot causing brown spots; healthy plants tolerate it well.",
                new String[]{"Maintain plant vigour with balanced nutrition.",
                        "Ensure adequate spacing.",
                        "Remove severely infected leaves."},
                "Rarely needs chemical control; focus on crop health. Confirm any need with a MOFA/PPRSD officer.",
                "warn", "MOFA / PPRSD"));

        MAP.put("cassava_green_mite", new Info("Cassava Green Mite",
                "A mite that feeds on young leaves and shoots, causing mottling and 'candle-stick' tips, worst in dry season.",
                new String[]{"Use clean planting material and tolerant varieties.",
                        "Plant early at the start of the rains.",
                        "Conserve natural enemies — avoid broad-spectrum insecticides."},
                "Chemical miticides are generally NOT recommended (they disrupt Ghana's established biological control). Rely on tolerant varieties and natural enemies.",
                "warn", "MOFA / PPRSD"));

        MAP.put("cassava_healthy", new Info("Healthy Cassava",
                "No disease detected. The leaf appears healthy.",
                new String[]{"Keep using clean planting material.",
                        "Favour resistant varieties and early planting.",
                        "Scout the field regularly."},
                "No treatment needed. Re-scan if symptoms appear.",
                "healthy", "MOFA / PPRSD"));

        MAP.put("cassava_mosaic", new Info("Cassava Mosaic Disease",
                "A viral disease (spread by whiteflies and infected cuttings) causing yellow-green mosaic, leaf distortion and stunting; can cause heavy yield loss.",
                new String[]{"Plant resistant/tolerant varieties — the main control.",
                        "Use virus-free planting material.",
                        "Rogue and destroy infected plants early.",
                        "Use clean stock when exchanging cuttings."},
                "No chemical cure for the virus. Rely on resistant varieties and clean planting material. Confirm varieties with a MOFA/PPRSD officer.",
                "alert", "MOFA / PPRSD"));

        // ---- MAIZE (MOFA / PPRSD) ----
        MAP.put("maize_fall_armyworm", new Info("Fall Armyworm",
                "A major caterpillar pest (Spodoptera frugiperda) that feeds in the whorl, causing ragged holes and 'windowing'; can devastate maize.",
                new String[]{"Scout early and often; check the whorl.",
                        "Hand-pick and crush egg masses and larvae on small plots.",
                        "Plant early; rotate and intercrop.",
                        "Apply control into the funnel at early larval stage."},
                "Biorational options work in Ghana: Bacillus thuringiensis (Bt) and neem-based products; emamectin benzoate is an effective synthetic. Apply into the whorl. Confirm the currently-registered product with a MOFA/PPRSD officer.",
                "alert", "MOFA / PPRSD"));

        MAP.put("maize_grasshoper", new Info("Grasshopper Damage",
                "Grasshoppers chew maize leaves and can defoliate young plants.",
                new String[]{"Manage weeds and field margins that shelter them.",
                        "Scout seedlings early.",
                        "Hand-pick on small plots."},
                "Spot-treat with a recommended insecticide only when damage is significant. Confirm the product with a MOFA/PPRSD officer.",
                "warn", "MOFA / PPRSD"));

        MAP.put("maize_healthy", new Info("Healthy Maize",
                "No disease or pest detected. The leaf appears healthy.",
                new String[]{"Use certified/clean seed and good spacing.",
                        "Keep up balanced fertilisation and weed control.",
                        "Keep scouting the whorl, especially for fall armyworm."},
                "No treatment needed. Re-scan if symptoms appear.",
                "healthy", "MOFA / PPRSD"));

        MAP.put("maize_leaf_beetle", new Info("Maize Leaf Beetle",
                "Beetles that chew leaves, leaving shot-holes and skeletonised patches, worst on young plants.",
                new String[]{"Plant early; keep fields weed-free.",
                        "Scout seedlings, the most vulnerable stage.",
                        "Rotate crops."},
                "Insecticide only at damaging thresholds on young plants; prefer reduced-impact products. Confirm with a MOFA/PPRSD officer.",
                "warn", "MOFA / PPRSD"));

        MAP.put("maize_leaf_blight", new Info("Maize Leaf Blight",
                "A fungal disease creating long grey-green to tan lesions on leaves, reducing photosynthesis and grain fill.",
                new String[]{"Plant resistant/tolerant varieties.",
                        "Rotate crops and manage infected residue.",
                        "Avoid dense canopies; ensure good spacing."},
                "A mancozeb-type foliar fungicide may be justified on susceptible varieties under high pressure; for most smallholders, resistant varieties and rotation are the practical control. Confirm with a MOFA/PPRSD officer.",
                "warn", "MOFA / PPRSD"));

        MAP.put("maize_leaf_spot", new Info("Maize Leaf Spot",
                "Grey leaf spot and related fungal spots causing small rectangular lesions between leaf veins.",
                new String[]{"Plant resistant varieties.",
                        "Rotate crops and manage residue.",
                        "Space plants for airflow."},
                "Fungicide only where economically justified; resistant varieties and rotation are the mainstay. Confirm with a MOFA/PPRSD officer.",
                "warn", "MOFA / PPRSD"));

        MAP.put("maize_streak_virus", new Info("Maize Streak Virus",
                "A viral disease (spread by leafhoppers) causing narrow chlorotic streaks along leaf veins; significant in parts of Ghana.",
                new String[]{"Plant resistant/tolerant varieties — the main control.",
                        "Plant early and uniformly to avoid leafhopper peaks.",
                        "Rogue severely infected plants; manage grassy hosts."},
                "No chemical cure for the virus. Rely on resistant varieties and planting timing. Confirm varieties with a MOFA/PPRSD officer.",
                "alert", "MOFA / PPRSD"));
    }

    public static Info get(String label) {
        Info info = MAP.get(label);
        if (info != null) return info;
        // Fallback for any unmapped label
        return new Info(label, "Information not available for this class.",
                new String[]{"Consult a MOFA/COCOBOD extension officer."},
                "Confirm diagnosis and treatment with an extension officer.",
                "warn", "");
    }
}