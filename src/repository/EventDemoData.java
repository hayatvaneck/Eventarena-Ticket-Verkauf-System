package repository;

import domain.Event.MapType;
import domain.Event.EventType;
import domain.layout.HallLayoutFactory;
import domain.Event;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Stellt die Demo-Events für den Erststart bereit.
 */
public final class EventDemoData {

    /** Verhindert die Instanziierung der reinen Demo-Datenklasse. */
    private EventDemoData() {
    }

    /**
     * Befüllt die übergebene Liste mit den vordefinierten Beispielveranstaltungen.
     *
     * @param target Zielliste für die Demo-Events
     */
    public static void seedInto(List<Event> target) {
        if (target == null) {
            return;
        }

        add(target,
            1L,
            "Don Toliver Octane Tour Leg 2",
            """
            Erleben Sie Don Toliver live mit seinem unverwechselbaren Mix aus Hip-Hop, Trap und R&B.
            Freuen Sie sich auf eine energiegeladene Show mit seinen größten Hits, beeindruckenden Lichteffekten und einer mitreißenden Atmosphäre.
            Ein unvergesslicher Abend für alle Fans moderner Rap-Musik.
            """,
            EventType.KONZERT,
            LocalDateTime.of(2026, 11, 2, 19, 0),
            100.0,
            MapType.STAGE_STANDING);

        add(target,
            2L,
            "Klassik Gala",
            """
            Erleben Sie einen festlichen Abend mit einem abwechslungsreichen Programm aus den bekanntesten Werken der klassischen Musik.
            Freuen Sie sich auf herausragende Solistinnen und Solisten, ein renommiertes Orchester sowie eine stilvolle Atmosphäre,
            die Musikliebhaberinnen und Musikliebhaber gleichermaßen begeistern wird.
            """,
            EventType.GALA,
            LocalDateTime.of(2026, 12, 15, 20, 0),
            150.0,
            MapType.STAGE_SEATED);

        add(target,
            3L,
            "Alba Berlin vs. FC Bayern München",
            """
            Im Rahmen der regulären Saison der Basketball-Bundesliga treffen ALBA Berlin und der FC Bayern Basketball aufeinander.
            Beide Mannschaften zählen seit Jahren zu den erfolgreichsten Teams der Liga und versprechen ein intensives Duell auf hohem sportlichem Niveau.
            Das Spiel findet in einer modernen Arena mit nummerierten Sitzplätzen und guter Sicht auf das Spielfeld statt.
            """,
            EventType.SPORTS,
            LocalDateTime.of(2026, 8, 11, 18, 0),
            80.0,
            MapType.ARENA);

        add(target,
            4L,
            "Dance Masters",
            """
            Erleben Sie die besten Tänzerinnen und Tänzer in einem spannenden Wettbewerb, bei dem verschiedene Tanzstile von klassischem Ballett bis hin zu modernem Streetdance präsentiert werden.
            Die Teilnehmerinnen und Teilnehmer zeigen ihr Können in beeindruckenden Choreografien und treten gegeneinander an, um den Titel des Dance Masters zu gewinnen.
            """,
            EventType.TANZ,
            LocalDateTime.of(2026, 7, 30, 18, 30),
            35.0,
            MapType.ARENA);

        add(target,
            5L,
            "Battle of the Rhymes",
            """
            Mach dich bereit für einen Abend voller Punchlines, Flow und harter Konter.
            Erlebe, wie die MCs Runde für Runde um den Sieg battlen und mit kreativen Bars und spontanen Freestyles das Publikum überzeugen.
            Ob du wegen der Technik, der Energie oder der Atmosphäre kommst – hier feierst du Battle-Rap so, wie er sein soll: laut, direkt und mit einer Crowd, die jede starke Line spürbar macht.
            """,
            EventType.KONZERT,
            LocalDateTime.of(2026, 10, 4, 19, 30),
            25.0,
            MapType.STAGE_STANDING);

        add(target,
            6L,
            "Hamlet",
            """
            Erleben Sie das zeitlose Drama 'Hamlet' von William Shakespeare in einer fesselnden Inszenierung.
            Tauchen Sie ein in die Welt von Intrigen, Rache und Tragik, während talentierte Schauspielerinnen und Schauspieler die komplexen Charaktere zum Leben erwecken.
            """,
            EventType.THEATER,
            LocalDateTime.of(2026, 9, 12, 18, 0),
            45.0,
            MapType.STAGE_SEATED);

        add(target,
            7L,
            "Starlight Dreams",
            """
            "Starlight Dreams" erzählt die Geschichte einer jungen Musikerin, die ihren Traum von einer Karriere auf der großen Bühne verfolgt.
            Begleitet wird die Handlung von eigens komponierten Songs, aufwendigen Choreografien und einem abwechslungsreichen Bühnenbild.
            Das Musical richtet sich an Besucherinnen und Besucher jeden Alters und entführt das Publikum in eine faszinierende Welt von Liebe, Leidenschaft und Geheimnissen.
            """,
            EventType.MUSICAL,
            LocalDateTime.of(2026, 8, 22, 20, 0),
            50.0,
            MapType.STAGE_SEATED);

        add(target,
            8L,
            "Comedy Show: The Laugh Factory",
            """
            Genießen Sie einen Abend voller Humor mit bekannten Comedians und aufstrebenden Nachwuchstalenten.
            Freuen Sie sich auf schlagfertige Stand-up-Comedy, spontane Improvisationen und unterhaltsame Geschichten aus dem Alltag.
            Ob feinsinniger Wortwitz oder pointierte Gesellschaftssatire – hier bleibt garantiert kein Auge trocken.
            """,
            EventType.COMEDY,
            LocalDateTime.of(2026, 11, 19, 19, 0),
            45.0,
            MapType.STAGE_SEATED);

        add(target,
            9L,
            "TechTalks",
            """
            Erleben Sie die beliebte TechTalks-Podcastreihe live auf der Bühne.
            Hören Sie spannende Diskussionen über aktuelle Technologietrends, Innovationen und die Zukunft der digitalen Welt.
            Die Hosts und Gäste teilen ihre Erfahrungen, beantworten Fragen aus dem Publikum und bieten exklusive Einblicke hinter die Kulissen der Tech-Branche.
            """,
            EventType.OTHER,
            LocalDateTime.of(2026, 12, 6, 18, 30),
            30.0,
            MapType.STAGE_SEATED);

        add(target,
            10L,
            "Electronic Pulse Festival",
            """
            Erleben Sie ein mitreißendes Elektronikfestival mit pulsierenden Beats, starken Lichtshows und einer energiegeladenen Atmosphäre.
            Verschiedene DJs und Live-Acts sorgen für einen unvergesslichen Abend voller Bewegung und Stimmung auf der gesamten Fläche.
            """,
            EventType.KONZERT,
            LocalDateTime.of(2026, 9, 27, 21, 0),
            110.0,
            MapType.STAGE_STANDING);
    }

    /**
     * Erzeugt ein Demo-Event, wendet das passende Hallenlayout an und fügt es ein.
     *
     * @param target Zielliste
     * @param id Event-ID
     * @param title Titel
     * @param description Beschreibung
     * @param eventType Eventkategorie
     * @param dateTime Beginn
     * @param basePrice Basispreis
     * @param mapType Saalplantyp
     */
    private static void add(List<Event> target, Long id, String title, String description, EventType eventType, LocalDateTime dateTime, double basePrice, MapType mapType) {
        Event event = new Event(id, title, description, eventType, dateTime, basePrice, mapType);
        HallLayoutFactory.applyLayoutForMapType(event);
        target.add(event);
    }
}
