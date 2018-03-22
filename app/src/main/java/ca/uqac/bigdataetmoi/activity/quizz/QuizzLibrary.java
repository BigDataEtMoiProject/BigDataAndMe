package ca.uqac.bigdataetmoi.activity.quizz;

/**
 * Created by Maxime Berthet on 23/11/2017.
 */

@SuppressWarnings("HardCodedStringLiteral")
class QuizzLibrary {

    String PersoQuestions [] = {
            "Si tu dois créer une adresse mail il vaut mieux :",
            "Avant d'installer une application il vaut mieux :",
            "Si j'ai déjà une application mobile est déjà installée sur mon téléphone et que je doute des données qu'elle peut prendre sur moi :",
            "Quand je marche dans la rue avec mon téléphone dans la poche :"
    };

    private String PersoChoices [][] = {
            {"Creer une adresse Yahoo ou Hotmail c'est toujours mieux que Google.",
                    "Creer un compte Google c'est le plus simple sur android.",
                    "Choisir un fournisseur local et vérifier que les données sont hébergées dans ton pays quitte à payer plus chère."},
            {"Vérifier ni les règles de confidentialité ni le détail des autorisation sa prend trop de temps.",
                    "Vérifier que l'application soit gratuite même si elle a des Règles de confidentialités moins sécurisés.",
                    "Vérifier le détail des autorisations et les Regles de confidentialités quitte à acheter une application payante si elle est mieux sécurisé."},
            {"Je n'ai pas le temps de m'en soucier",
                    "Je vais voir l'application dans les paramètres et vérifie les autorisations",
                    "Si des informations ont été prisent ce n'est pas grave, c'est ce qui rend l'application gratuite"},
            {"Je coupe mon gps pour qu'on ne puisse pas tracer mon parcours",
                    "Je me déplace en utilisant google map pour trouver le moyen de déplacement le moins chère",
                    "Je laisse le gps de mon téléphone c'est plus rapide si je veux utiliser google map"}
    };

    private String MoneyAnswers[] = {"Creer une adresse Yahoo ou Hotmail c'est toujours mieux que Google.",
            "Vérifier que l'application soit gratuite même si elle a des Règles de confidentialités moins sécurisés.",
            "Si des informations ont été prisent ce n'est pas grave, c'est ce qui rend l'application gratuite",
            "Je me déplace en utilisant google map pour trouver le moyen de déplacement le moins chère"};

    private String TimeAnswers[] = {"Creer un compte Google c'est le plus simple sur android.",
            "Vérifier ni les règles de confidentialité ni le détail des autorisation sa prend trop de temps.",
            "Je n'ai pas le temps de m'en soucier",
            "Je laisse le gps de mon téléphone c'est plus rapide si je veux utiliser google map"};

    private String SecurityAnswers[] = {"Choisir un fournisseur local et vérifier que les données sont hébergées dans ton pays quitte à payer plus chère.",
            "Vérifier le détail des autorisations et les Regles de confidentialités quitte à acheter une application payante si elle est mieux sécurisé.",
            "Je vais voir l'application dans les paramètres et vérifie les autorisations",
            "Je coupe mon gps pour qu'on ne puisse pas tracer mon parcours"};

    private static String[] facileQuestions  = {
            "FQ1 :", "FQ2 :","FQ3 :", "FQ4 :", "FQ5 :", "FQ6 :","FQ7 :", "FQ8 :", "FQ9 :", "FQ10 :",
            "FQ11 :", "FQ12 :","FQ13 :", "FQ14 :", "FQ15 :", "FQ16 :","FQ17 :", "FQ18 :", "FQ19 :", "FQ20 :",
            "FQ21 :", "FQ22 :","FQ23 :", "FQ24 :", "FQ25 :", "FQ26 :","FQ27 :", "FQ28 :", "FQ29 :", "FQ30 :",
            "FQ31 :", "FQ32 :","FQ33 :", "FQ34 :", "FQ35 :", "FQ36 :","FQ37 :", "FQ38 :", "FQ39 :", "FQ40 :",
            "FQ41 :", "FQ42 :","FQ43 :", "FQ44 :", "FQ45 :", "FQ46 :","FQ47 :", "FQ48 :", "FQ49 :", "FQ50 :"
    };

    private static String[][] facileChoices  = {
            {"FQ1F1 :", "FQ1F2 :","FQ1C :"},{"FQ2F1 :", "FQ2C :","FQ2F2 :"},{"FQ3F1 :", "FQ3F2 :","FQ3C :"},{"FQ4F1 :", "FQ4F2 :","FQ4C :"},{"FQ5C :", "FQ5F2 :","FQ5F1 :"},{"FQ6F1 :", "FQ6F2 :","FQ6C :"},{"FQ7F2 :", "FQ7F1 :","FQ1C :"},{"FQ8C :", "FQ8F1 :","FQ8F2 :"},{"FQ9F2 :", "FQ9F1 :","FQ9C :"},{"FQ10F2 :", "FQ10F1 :","FQ10C :"},
            {"FQ11F1 :", "FQ11F2 :","FQ11C :"},{"FQ12F1 :", "FQ12C :","FQ12F2 :"},{"FQ13F1 :", "FQ13F2 :","FQ13C :"},{"FQ14F1 :", "FQ14F2 :","FQ14C :"},{"FQ15C :", "FQ15F2 :","FQ15F1 :"},{"FQ16F1 :", "FQ16F2 :","FQ16C :"},{"FQ17F2 :", "FQ17F1 :","FQ17C :"},{"FQ18C :", "FQ18F1 :","FQ18F2 :"},{"FQ9F2 :", "FQ19F1 :","FQ19C :"},{"FQ20F2 :", "FQ20F1 :","FQ20C :"},
            {"FQ21F1 :", "FQ21F2 :","FQ21C :"},{"FQ22F1 :", "FQ22C :","FQ22F2 :"},{"FQ23F1 :", "FQ23F2 :","FQ23C :"},{"FQ24F1 :", "FQ24F2 :","FQ24C :"},{"FQ25C :", "FQ25F2 :","FQ25F1 :"},{"FQ26F1 :", "FQ26F2 :","FQ26C :"},{"FQ27F2 :", "FQ27F1 :","FQ27C :"},{"FQ28C :", "FQ28F1 :","FQ28F2 :"},{"FQ9F2 :", "FQ29F1 :","FQ29C :"},{"FQ30F2 :", "FQ30F1 :","FQ30C :"},
            {"FQ31F1 :", "FQ31F2 :","FQ31C :"},{"FQ32F1 :", "FQ32C :","FQ32F2 :"},{"FQ33F1 :", "FQ33F2 :","FQ33C :"},{"FQ34F1 :", "FQ34F2 :","FQ34C :"},{"FQ35C :", "FQ35F2 :","FQ35F1 :"},{"FQ36F1 :", "FQ36F2 :","FQ36C :"},{"FQ37F2 :", "FQ37F1 :","FQ37C :"},{"FQ38C :", "FQ38F1 :","FQ38F2 :"},{"FQ9F2 :", "FQ39F1 :","FQ39C :"},{"FQ40F2 :", "FQ40F1 :","FQ40C :"},
            {"FQ41F1 :", "FQ41F2 :","FQ41C :"},{"FQ42F1 :", "FQ42C :","FQ42F2 :"},{"FQ43F1 :", "FQ43F2 :","FQ43C :"},{"FQ44F1 :", "FQ44F2 :","FQ44C :"},{"FQ45C :", "FQ45F2 :","FQ45F1 :"},{"FQ46F1 :", "FQ46F2 :","FQ46C :"},{"FQ47F2 :", "FQ47F1 :","FQ47C :"},{"FQ48C :", "FQ48F1 :","FQ48F2 :"},{"FQ9F2 :", "FQ49F1 :","FQ49C :"},{"FQ50F2 :", "FQ50F1 :","FQ50C :"}
    };

    private static String[] facileCorrectAnswers = {
            "FQ1C :", "FQ2C :","FQ3C :", "FQ4C :", "FQ5C :", "FQ6C :","FQ7C :", "FQ8C :", "FQ9C :", "FQ10C :",
            "FQ11C :", "FQ12C :","FQ13C :", "FQ14C :", "FQ15C :", "FQ16C :","FQ17C :", "FQ18C :", "FQ19C :", "FQ20C :",
            "FQ21C :", "FQ22C :","FQ23C :", "FQ24C :", "FQ25C :", "FQ26C :","FQ27C :", "FQ28C :", "FQ29C :", "FQ30C :",
            "FQ31C :", "FQ32C :","FQ33C :", "FQ34C :", "FQ35C :", "FQ36C :","FQ37C :", "FQ38C :", "FQ39C :", "FQ40C :",
            "FQ41C :", "FQ42C :","FQ43C :", "FQ44C :", "FQ45C :", "FQ46C :","FQ47C :", "FQ48C :", "FQ49C :", "FQ50C :"
    };

    String getPersoQuestion(int a) {
        return PersoQuestions[a];
    }

    String getPersoChoice1(int a) {
        return PersoChoices[a][0];
    }

    String getPersoChoice2(int a) {
        return PersoChoices[a][1];
    }

    String getPersoChoice3(int a) {
        return PersoChoices[a][2];
    }

    String getMoneyAnswer(int a) {
        return MoneyAnswers[a];
    }

    String getTimeAnswer(int a) {
        return TimeAnswers[a];
    }

    String getSecurityAnswer(int a) {
        String answer = SecurityAnswers[a];
        return answer;
    }

    static String getFacileQuestion(int a) {
        String question = facileQuestions[a];
        return question;
    }

    static String getFacileChoice1(int a) {
        String choice0 = facileChoices[a][0];
        return choice0;
    }

    static String getFacileChoice2(int a) {
        String choice1 = facileChoices[a][1];
        return choice1;
    }

    static String getFacileChoice3(int a) {
        String choice2 = facileChoices[a][2];
        return choice2;
    }

    static String getFacileCorrectAnswer(int a) {
        String answer = facileCorrectAnswers[a];
        return answer;
    }

}