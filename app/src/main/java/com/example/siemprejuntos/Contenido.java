package com.example.siemprejuntos;

import java.util.Calendar;

/**
 * Textos de la aplicación: mensajes del día y tutoriales paso a paso.
 * Todo está escrito por el equipo de SiempreJuntos (no es contenido generado por IA).
 */
public final class Contenido {

    private Contenido() {
    }

    // ------------------------------------------------------------ mensajes del día

    private static final String[] MENSAJES = {
            "Qué alegría que hoy esté aquí. Su familia piensa en usted con mucho cariño.",
            "Hoy es un día para agradecer lo bueno, aunque sea pequeño: un café calentito, el sol, una voz querida.",
            "Usted es muy valioso(a). Cada día que pasa, su historia nos enseña algo hermoso.",
            "Qué lindo es empezar un día nuevo. Respire hondo y sonría un poquito: se lo merece.",
            "El día está para disfrutarlo con calma. Hoy no hay apuro: lo importante es sentirse bien.",
            "No está solo(a). Hay personas que lo quieren y que hoy se acuerdan de usted.",
            "Las flores se abren despacito, cada una a su tiempo. Usted también puede ir despacito hoy.",
            "Un abrazo grande de parte de todos los que lo quieren. Hoy es un buen día para recibirlo.",
            "Sus recuerdos son un tesoro. Hoy puede mirar una foto que le alegre el corazón.",
            "Cada mañana trae una nueva oportunidad de sentirse querido(a). Hoy también.",
            "Hoy el solo hecho de estar aquí ya es un motivo para celebrar. ¡Feliz día!",
            "Cuando el ánimo esté pesado, converse con alguien de confianza. Compartir alivia el corazón.",
            "Una canción conocida, un té tibio y un rato de calma: así se arma un día hermoso.",
            "Usted ha vivido mucho y ha superado muchas cosas. Eso lo hace fuerte y admirable.",
            "Una llamada corta puede alegrar el día de dos personas: el suyo y el de quien llama.",
            "Si hoy se siente cansado(a), descanse sin culpa. Mañana será otro día, y estaremos aquí.",
            "Hoy puede mirar por la ventana y buscar algo lindo: una nube, un pajarito, una flor.",
            "Cuidar de usted también es una forma de querer a su familia. Tome agua y coma algo rico.",
            "Moverse suavemente un ratito le hace bien al cuerpo y también al ánimo.",
            "Hay días grises, pero siempre vuelve la luz. Hoy vamos a buscarla juntos.",
            "Su sonrisa vale mucho. Regálela hoy a alguien, o a usted mismo(a) frente al espejo.",
            "Contarle a alguien cómo se siente es un acto de valentía y de cariño."
    };

    /** Mensaje cariñoso del día. Con el espacio de fe activado, es un pensamiento de ánimo con su cita. */
    public static String mensajeDelDia(boolean conFe) {
        int dia = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (conFe) {
            Pasaje p = pasajeDelDia();
            return p.pensamiento + "  (" + p.referencia + ")";
        }
        return MENSAJES[dia % MENSAJES.length];
    }

    public static String mensajeDelDia() {
        return mensajeDelDia(false);
    }


    // ------------------------------------------------------- preguntas del corazón

    /** Una pregunta cariñosa con tres respuestas posibles y un cierre de ánimo. */
    public static final class Corazon {
        public final int momento;         // 0 mañana, 1 tarde, 2 noche
        public final String pregunta;
        public final String inspiracion;  // frase breve (con cita si es del espacio de fe)
        public final String[] opciones;
        public final String cierre;

        Corazon(int momento, String pregunta, String inspiracion, String[] opciones, String cierre) {
            this.momento = momento;
            this.pregunta = pregunta;
            this.inspiracion = inspiracion;
            this.opciones = opciones;
            this.cierre = cierre;
        }
    }

    private static Corazon c(int m, String p, String i, String a, String b, String d, String cierre) {
        return new Corazon(m, p, i, new String[]{a, b, d}, cierre);
    }

    /** Preguntas generales, llenas de cariño y energía positiva (sin contenido religioso). */
    private static final Corazon[] CORAZON_GENERAL = {
            c(0, "¿Qué cosa linda le gustaría que pasara hoy?", "Los buenos días empiezan con una buena esperanza.",
                    "😊  Hablar con alguien querido", "🌤️  Un día tranquilo", "🎵  Escuchar buena música",
                    "Qué lindo deseo. Ojalá se cumpla. ¡Que tenga un día hermoso!"),
            c(0, "¿Cómo le gustaría comenzar este día?", "Cada mañana es una hoja en blanco para escribir algo bonito.",
                    "☕  Con algo calentito", "🪟  Mirando el cielo", "📞  Con una llamada",
                    "Esa es una linda manera de empezar. ¡Disfrútela con calma!"),
            c(0, "¿A quién le gustaría alegrarle el día hoy?", "Una palabra cariñosa vale más que mil regalos.",
                    "👨‍👩‍👧  A un familiar", "🧑‍🤝‍🧑  A un amigo o vecino", "💛  A mí mismo(a)",
                    "Qué corazón generoso. Alegrar a otros también nos alegra a nosotros."),
            c(0, "¿Por qué cosa pequeña está agradecido(a) esta mañana?", "Agradecer abre la puerta a un día más luminoso.",
                    "☀️  Por un nuevo día", "👪  Por mi familia", "🏠  Por lo que tengo",
                    "Qué bonito agradecer. Esa gratitud le hace bien al corazón."),
            c(1, "¿Qué fue lo más lindo que le pasó hoy?", "Siempre hay algo bueno escondido en el día, aunque sea chiquito.",
                    "😊  Una sonrisa", "📞  Una llamada o mensaje", "🌞  Un momento de calma",
                    "Guárdelo en el corazón. Los momentos lindos se multiplican al recordarlos."),
            c(1, "¿En quién pensó hoy con cariño?", "Pensar en quienes queremos nos acerca a ellos.",
                    "👨‍👩‍👧  En mi familia", "🧑‍🤝‍🧑  En un amigo", "🕊️  En alguien que recuerdo",
                    "Qué hermoso. Si quiere, hoy puede mandarle un saludo."),
            c(1, "¿Qué le gustaría hacer esta tarde para sentirse bien?", "Un pequeño gusto también es cuidarse.",
                    "🎶  Escuchar música", "🫖  Tomar algo rico", "📷  Mirar fotos lindas",
                    "Qué buena idea. Se lo merece."),
            c(2, "¿Qué momento bueno se lleva de este día?", "Antes de dormir, se descansa mejor con un recuerdo bonito.",
                    "😊  Algo que alguien me dijo", "🌅  Algo que vi", "🍽️  Algo que comí",
                    "Qué lindo. Que ese recuerdo lo acompañe esta noche."),
            c(2, "¿Cómo se siente el corazón esta noche?", "Mañana vuelve a salir el sol y aquí estaremos otra vez.",
                    "💚  Tranquilo", "🙂  Más o menos", "🤍  Con ganas de compañía",
                    "Gracias por contarme. Descanse bien. Mañana será otro día lleno de posibilidades."),
            c(2, "¿Por qué cosa pequeña está agradecido(a) esta noche?", "Dormir agradecido(a) es dormir en paz.",
                    "🏠  Por mi casa", "👪  Por quienes me quieren", "🌙  Por este día",
                    "Qué hermoso. Que tenga una noche tranquila y llena de paz."),
    };

    /** Preguntas inspiradas en la Biblia, escritas con palabras propias (solo con el espacio de fe activado). */
    private static final Corazon[] CORAZON_FE = {
            c(0, "¿Qué cosa buena le gustaría que pasara hoy?", "Un corazón alegre hace bien (Proverbios 17:22).",
                    "😊  Hablar con alguien querido", "🌤️  Un día tranquilo", "🙏  Sentirme acompañado(a)",
                    "Qué lindo deseo. Empiece el día con esperanza: no camina solo(a)."),
            c(0, "¿Por qué cosa está agradecido(a) esta mañana?", "Es bueno dar gracias a Jehová (Salmo 92:1).",
                    "☀️  Por un nuevo día", "👪  Por mi familia", "🏠  Por lo que tengo",
                    "Qué bonito agradecer. La gratitud llena el corazón de luz."),
            c(0, "¿A quién le diría hoy una palabra amable?", "Las palabras amables son como la miel, dulces para el ánimo (Proverbios 16:24).",
                    "👨‍👩‍👧  A un familiar", "🧑‍🤝‍🧑  A un amigo o vecino", "💛  A mí mismo(a)",
                    "Qué corazón generoso. Una palabra buena puede alegrar todo un día."),
            c(0, "¿Cómo le gustaría comenzar el día?", "Confíe en Jehová de todo corazón y él dirigirá sus pasos (Proverbios 3:5, 6).",
                    "🙏  Con una oración", "☕  Con calma y algo rico", "🎵  Con buena música",
                    "Qué hermosa forma de comenzar. Que este día esté lleno de paz."),
            c(0, "¿Qué le da fuerzas para comenzar hoy?", "Si Jehová está de su parte, no hay por qué tener miedo (Salmo 118:6).",
                    "👪  Mi familia", "🙏  Mi fe", "🌅  Un día nuevo",
                    "Qué lindo. Tiene motivos para sonreír hoy. ¡Ánimo!"),
            c(1, "¿Qué fue lo más lindo del día hasta ahora?", "Un corazón alegre hace bien (Proverbios 17:22).",
                    "😊  Una sonrisa", "📞  Una llamada o mensaje", "🌞  Un momento de calma",
                    "Qué bueno. Los momentos lindos son regalos: agradézcalos."),
            c(1, "¿Hay algo que quiera dejar en manos de Jehová hoy?", "Puede echar sobre él toda su inquietud, porque él se interesa por usted (1 Pedro 5:7).",
                    "💭  Sí, una preocupación", "😌  No, estoy tranquilo(a)", "🤍  Prefiero no decirlo",
                    "Puede contarle en oración lo que siente. Él lo escucha y usted no está solo(a)."),
            c(1, "¿Cómo está su ánimo hoy?", "Jehová está cerca de los que tienen el corazón quebrantado (Salmo 34:18).",
                    "😊  Con alegría", "😐  Con altibajos", "😟  Con un poco de tristeza",
                    "Gracias por contarme. Jehová lo entiende y su familia lo quiere. Ánimo."),
            c(2, "¿Qué momento bueno se lleva de este día?", "Cada día tiene lo suyo; viva con calma (Mateo 6:34).",
                    "😊  Algo que alguien me dijo", "🌅  Algo que vi", "🙏  Un momento de oración",
                    "Qué lindo. Que ese recuerdo lo acompañe esta noche."),
            c(2, "¿Qué le gustaría agradecer esta noche?", "Su paz puede cuidar el corazón y los pensamientos (Filipenses 4:6, 7).",
                    "🏠  Mi casa", "👪  Quienes me quieren", "🌙  Este día",
                    "Qué hermoso. Descanse en paz: mañana vuelve a salir el sol."),
            c(2, "¿Hay algo que le preocupe antes de dormir?", "Puede dejar su carga en manos de Jehová (Salmo 55:22).",
                    "💭  Sí, algo", "😌  No, estoy en paz", "🤍  Prefiero no decirlo",
                    "Entréguele en oración lo que le inquieta. Descanse: usted no está solo(a)."),
    };

    /** Elige al azar una pregunta del corazón según la hora del día. */
    public static Corazon preguntaDelCorazon(boolean conFe, int hora) {
        int momento = hora < 12 ? 0 : (hora < 19 ? 1 : 2);
        Corazon[] fuente = conFe ? CORAZON_FE : CORAZON_GENERAL;
        java.util.List<Corazon> candidatas = new java.util.ArrayList<>();
        for (Corazon x : fuente) {
            if (x.momento == momento) {
                candidatas.add(x);
            }
        }
        return candidatas.get(new java.util.Random().nextInt(candidatas.size()));
    }

    /** Una idea sencilla según la hora del día. */
    public static String ideaSegunHora() {
        int h = Util.horaActual();
        if (h < 11) {
            return "☀️ Buen momento para abrir la ventana, tomar desayuno y tomar un vaso de agua.";
        } else if (h < 15) {
            return "🍽️ Hora de almorzar con calma. Después, un ratito de descanso.";
        } else if (h < 19) {
            return "📞 Buena hora para llamar a alguien querido o escuchar música.";
        }
        return "🌙 Una infusión tibia, música suave y a descansar. Mañana será un buen día.";
    }

    // ---------------------------------------------------------------- tutoriales

    public static final class Leccion {
        public final String icono;
        public final String titulo;
        public final String[] pasos;
        public final String consejo;
        public final String busquedaVideo;

        Leccion(String icono, String titulo, String[] pasos, String consejo, String busquedaVideo) {
            this.icono = icono;
            this.titulo = titulo;
            this.pasos = pasos;
            this.consejo = consejo;
            this.busquedaVideo = busquedaVideo;
        }
    }

    public static final Leccion[] LECCIONES = {
            new Leccion("📍", "Mandar mi ubicación por WhatsApp", new String[]{
                    "Abra WhatsApp. Es el dibujo verde con un teléfono blanco.",
                    "Toque el nombre de la persona a quien quiere avisar.",
                    "Abajo, al lado de donde se escribe, toque el clip 📎 o el signo +.",
                    "Toque la palabra \"Ubicación\".",
                    "Toque \"Enviar mi ubicación actual\". Si el celular pregunta, toque \"Permitir\".",
                    "¡Listo! La otra persona verá un mapa con el lugar donde usted está."
            }, "Esto sirve cuando está perdido(a) o necesita que lo vayan a buscar.",
                    "cómo enviar mi ubicación por whatsapp paso a paso"),

            new Leccion("🎥", "Hacer una videollamada", new String[]{
                    "Abra WhatsApp.",
                    "Toque el nombre de la persona con quien quiere hablar.",
                    "Arriba a la derecha, toque el dibujo de la cámara de video 🎥.",
                    "Si el celular pregunta, toque \"Llamar\". Espere a que conteste.",
                    "Para terminar, toque el botón rojo del teléfono."
            }, "Si no se ve su cara, revise que la cámara no esté tapada con el dedo.",
                    "cómo hacer una videollamada por whatsapp paso a paso"),

            new Leccion("🎤", "Mandar un mensaje de voz", new String[]{
                    "Abra WhatsApp y toque el nombre de la persona.",
                    "Al lado derecho de donde se escribe, verá un micrófono 🎤.",
                    "Deje el dedo apretado sobre el micrófono y hable con calma.",
                    "Cuando termine, suelte el dedo. El audio se envía solo.",
                    "Si se arrepiente, antes de soltar deslice el dedo hacia la izquierda para cancelar."
            }, "Hablar es más fácil que escribir. Su familia se alegra al escuchar su voz.",
                    "cómo enviar un audio por whatsapp paso a paso"),

            new Leccion("📷", "Mandar una foto", new String[]{
                    "Abra WhatsApp y toque el nombre de la persona.",
                    "Toque el clip 📎 o el signo +, al lado de donde se escribe.",
                    "Toque \"Galería\" para elegir una foto que ya tiene, o \"Cámara\" para sacar una nueva.",
                    "Toque la foto que quiere enviar.",
                    "Toque el botón verde de enviar ➤."
            }, "Una foto de su día, de las plantas o de su comida alegra mucho a la familia.",
                    "cómo enviar una foto por whatsapp paso a paso"),

            new Leccion("📞", "Llamar a mi familia desde esta aplicación", new String[]{
                    "En la pantalla de inicio, toque \"Mi familia\".",
                    "Busque el nombre de la persona.",
                    "Toque el botón \"Llamar\". Se abrirá el teléfono con el número escrito.",
                    "Toque el botón verde de llamar del teléfono.",
                    "Para hablar por WhatsApp, toque el botón de WhatsApp de esa misma tarjeta."
            }, "Si no hay nombres en la lista, un familiar los puede agregar en Configuración.",
                    "cómo llamar por teléfono desde un celular android"),

            new Leccion("🆘", "Qué hacer si necesito ayuda", new String[]{
                    "Abajo en la pantalla de inicio hay un botón que dice \"Necesito ayuda\".",
                    "Tóquelo. La aplicación le preguntará si de verdad necesita ayuda.",
                    "Si es así, toque \"Sí, necesito ayuda\".",
                    "Se abrirá el teléfono con el 131 (ambulancia). Toque el botón verde para llamar.",
                    "También puede avisar a su familia por WhatsApp o mensaje, con el lugar donde está."
            }, "Si fue sin querer, toque \"No, fue sin querer\". No pasa nada.",
                    "cómo pedir ayuda en una emergencia con el celular")
    };

    // -------------------------------------------------------------- bienestar

    /** Pregunta de búsqueda por defecto para música. */
    public static final String BUSQUEDA_MUSICA_DEFECTO = "música para recordar los años 60 70 80";

    /**
     * La edad se usa SOLO para elegir la música: la de la juventud de la persona
     * (entre los 15 y los 25 años), que es la que más recuerdos buenos trae.
     * Devuelve {década inicial, década final}, por ejemplo {1970, 1980}; null si no se conoce la edad.
     */
    public static int[] decadasDeJuventud(int edad) {
        if (edad <= 0) {
            return null;
        }
        int nacimiento = Calendar.getInstance().get(Calendar.YEAR) - edad;
        int ini = (nacimiento + 15) / 10 * 10;
        int fin = (nacimiento + 25) / 10 * 10;
        return new int[]{ini, fin};
    }

    private static String nombreDecada(int decada) {
        return decada >= 2000 ? String.valueOf(decada) : String.valueOf(decada % 100);
    }

    /** Texto como "años 70 y 80", o null si no se conoce la edad. */
    public static String epocaMusical(int edad) {
        int[] d = decadasDeJuventud(edad);
        if (d == null) {
            return null;
        }
        return d[0] == d[1] ? "años " + nombreDecada(d[0])
                : "años " + nombreDecada(d[0]) + " y " + nombreDecada(d[1]);
    }

    /** Qué buscar en YouTube: el gusto que anotó la familia; si no, la época según la edad; si no, lo general. */
    public static String busquedaMusica(String gusto, int edad) {
        if (gusto != null && !gusto.trim().isEmpty()) {
            return gusto.trim() + " para escuchar";
        }
        String epoca = epocaMusical(edad);
        if (epoca != null) {
            return "música de los " + epoca + " en español para recordar";
        }
        return BUSQUEDA_MUSICA_DEFECTO;
    }
    public static final String BUSQUEDA_EJERCICIOS = "ejercicios suaves sentado para adultos mayores";
    public static final String BUSQUEDA_INFUSIONES = "infusiones y té caseros para la tarde recetas";
    public static final String BUSQUEDA_RESPIRACION = "ejercicio de respiración y relajación para adultos mayores";
    public static final String BUSQUEDA_MEMORIA = "juegos de memoria y mente activa para adultos mayores";

    public static final String AVISO_SALUD =
            "Estos videos son de apoyo y compañía, no reemplazan la atención médica. "
                    + "Antes de tomar una infusión nueva o hacer ejercicio, consulte a su médico, "
                    + "sobre todo si toma remedios o tiene alguna enfermedad.";

    // ------------------------------------------------------- palabras de ánimo (Biblia)

    /**
     * Un pasaje de ánimo. El "pensamiento" está escrito con palabras propias de SiempreJuntos;
     * el texto bíblico se lee en jw.org (Traducción del Nuevo Mundo) con el botón de la pantalla.
     */
    public static final class Pasaje {
        public final String referencia;
        public final String pensamiento;
        final String libro;
        final int capitulo;

        Pasaje(String referencia, String pensamiento, String libro, int capitulo) {
            this.referencia = referencia;
            this.pensamiento = pensamiento;
            this.libro = libro;
            this.capitulo = capitulo;
        }

        public String url() {
            return "https://www.jw.org/es/biblioteca/biblia/nwt/libros/" + libro + "/" + capitulo + "/";
        }
    }

    public static final String URL_BIBLIA = "https://www.jw.org/es/biblioteca/biblia/nwt/libros/";

    public static final Pasaje[] PASAJES = {
            new Pasaje("Isaías 41:10", "Jehová le dice que no tenga miedo, porque está a su lado. "
                    + "Él lo fortalece y lo sostiene.", "isaias", 41),
            new Pasaje("Salmo 23:1-4", "Jehová cuida a sus siervos como un pastor cuida a sus ovejas. "
                    + "Aun en los momentos más oscuros, usted no camina solo(a).", "salmos", 23),
            new Pasaje("Salmo 46:1", "Jehová es un refugio y una ayuda siempre cercana cuando las cosas se ponen difíciles.",
                    "salmos", 46),
            new Pasaje("Salmo 55:22", "Puede dejar sus cargas y preocupaciones en manos de Jehová. "
                    + "Él se encarga de sostenerlo.", "salmos", 55),
            new Pasaje("1 Pedro 5:7", "Jehová se interesa por usted. Puede contarle en oración todo lo que le preocupa.",
                    "1-pedro", 5),
            new Pasaje("Filipenses 4:6, 7", "Cuando algo lo inquiete, converse con Jehová en oración. "
                    + "Su paz puede cuidar el corazón y los pensamientos.", "filipenses", 4),
            new Pasaje("Salmo 34:18", "Jehová está cerca de quienes se sienten tristes y entiende cómo se siente usted.",
                    "salmos", 34),
            new Pasaje("Salmo 71:9, 18", "En la vejez, Jehová no abandona a sus siervos. "
                    + "Usted todavía tiene mucho que compartir con los demás.", "salmos", 71),
            new Pasaje("Isaías 46:4", "Jehová promete cuidar y sostener a sus siervos hasta la vejez y las canas.",
                    "isaias", 46),
            new Pasaje("Proverbios 16:31", "Las canas son una corona hermosa cuando se ha vivido haciendo lo bueno.",
                    "proverbios", 16),
            new Pasaje("Hebreos 13:5, 6", "Jehová promete no dejarlo ni abandonarlo nunca. Por eso puede sentir confianza.",
                    "hebreos", 13),
            new Pasaje("Santiago 4:8", "Si usted se acerca a Jehová, él se acerca a usted.", "santiago", 4),
            new Pasaje("Mateo 6:34", "No se angustie por el mañana. Cada día tiene lo suyo: viva hoy con calma.",
                    "mateo", 6),
            new Pasaje("Josué 1:9", "Jehová anima a ser valiente y a no asustarse, "
                    + "porque está con usted dondequiera que vaya.", "josue", 1),
            new Pasaje("Salmo 94:19", "Cuando muchos pensamientos le llenan la mente, "
                    + "el consuelo de Jehová alivia y calma.", "salmos", 94),
            new Pasaje("Revelación 21:3, 4", "Jehová promete un futuro sin llanto, sin dolor y sin tristeza.",
                    "revelacion", 21),

            // ---- Más Salmos
            new Pasaje("Salmo 27:1", "Con Jehová como luz y protección, no hay por qué vivir con miedo.",
                    "salmos", 27),
            new Pasaje("Salmo 37:3-5", "Confíe en Jehová y haga el bien. Entréguele su camino: él actuará a su favor.",
                    "salmos", 37),
            new Pasaje("Salmo 62:8", "Confíe en Jehová en todo momento y cuéntele con confianza lo que siente.",
                    "salmos", 62),
            new Pasaje("Salmo 92:12-14", "Quienes aman a Jehová pueden seguir dando buenos frutos "
                    + "incluso en la vejez.", "salmos", 92),
            new Pasaje("Salmo 118:6", "Si Jehová está de su parte, no hay por qué tener miedo.", "salmos", 118),
            new Pasaje("Salmo 119:105", "La Palabra de Dios es como una lámpara que alumbra el camino de cada día.",
                    "salmos", 119),
            new Pasaje("Salmo 121:1, 2", "La ayuda de usted viene de Jehová, el Creador del cielo y de la tierra.",
                    "salmos", 121),
            new Pasaje("Salmo 145:18", "Jehová está cerca de todos los que lo invocan con sinceridad.",
                    "salmos", 145),
            new Pasaje("Salmo 147:3", "Jehová consuela a los que tienen el corazón herido y cuida de sus heridas.",
                    "salmos", 147),

            // ---- Proverbios
            new Pasaje("Proverbios 3:5, 6", "Confíe en Jehová de todo corazón y no se apoye solo en lo que usted entiende. "
                    + "Él dirigirá sus pasos.", "proverbios", 3),
            new Pasaje("Proverbios 12:25", "La angustia pesa en el corazón, pero una palabra buena lo alegra. "
                    + "Hoy usted puede decir una a alguien.", "proverbios", 12),
            new Pasaje("Proverbios 16:3", "Ponga en manos de Jehová lo que hace y lo que planea, "
                    + "y sus planes se afirmarán.", "proverbios", 16),
            new Pasaje("Proverbios 16:24", "Las palabras amables son como la miel: dulces para el ánimo.",
                    "proverbios", 16),
            new Pasaje("Proverbios 17:22", "Un corazón alegre hace bien. Un ratito de risa y buena compañía cuenta mucho.",
                    "proverbios", 17),
            new Pasaje("Proverbios 18:10", "El nombre de Jehová es como una torre fuerte: "
                    + "quien confía en él se siente protegido.", "proverbios", 18)
    };

    public static Pasaje pasajeDelDia() {
        int dia = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return PASAJES[dia % PASAJES.length];
    }

    public static final String AVISO_FE =
            "Los pensamientos están escritos por SiempreJuntos. Para leer el texto de la Biblia, "
                    + "toque el botón de leer: se abre jw.org. No es una publicación oficial de ninguna "
                    + "organización religiosa.";
}
