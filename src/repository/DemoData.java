package repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import domain.*;

public class DemoData {

    public static List<Event> createDemoEvents() {
        List<Event> events = new ArrayList<>();

        events.add(createConcert());
        events.add(createGala());
        events.add(createSportEvent());
        events.add(createTheater());
        events.add(createMusical());
        events.add(createComedy());
        events.add(createLivePodcast());   
        events.add(createDanceCompetition());
        events.add(createRapBattle());
        
        return events;

    }

    
    // Erstellen der Events --> // Parameterübergabe: Long id, String title, String description, LocalDateTime dateTime, double basePrice
    //  und die zugehörigen Sections

      //                                   //
     //Events mit Bestuhlung im Innenraum// 
    //                                 //

    // Event 1: Konzert mit Stehplätze im Innenraum
    private static Event createConcert() {
        Event concert = new Event( 
            1L, 
            "Don Toliver Octane Tour Leg 2",
            """
            Erleben Sie einen energiegeladenen Konzertabend mit Don Toliver auf der Octane Tour Leg 2. 
            Freuen Sie sich auf eine beeindruckende Live-Show mit modernen Bühneneffekten, mitreißender Atmosphäre sowie einer Auswahl aktueller 
            Hits und beliebter Fan-Favoriten. Genießen Sie ein unvergessliches Konzerterlebnis, das Hip-Hop- und Trap-Fans gleichermaßen begeistern wird.
            """,
            LocalDateTime.of(2026, 11, 02, 19, 0), 
            60.0
            );
        concert.addSection(new StandingSection("Innenraum (Stehplatz)", 1.0, 2000));
        concert.addSection(new SeatedSection("Block 1", 1.2, 10, 20));
        concert.addSection(new SeatedSection("Block 2", 1.2, 10, 20));
        concert.addSection(new SeatedSection("Block 3", 1.2, 10, 20));
        concert.addSection(new SeatedSection("Block 4", 1.2, 10, 20));
        concert.addSection(new SeatedSection("Block 6", 0.8, 10, 20));
        concert.addSection(new SeatedSection("VIP", 2.5, 3, 15));
        return concert;
    }

    //Event Rapbattle - Innenraum ist Stehfläche
    private static Event createRapBattle(){
        Event rapBattle = new Event(
            9L,
            "Rap Battle: Battle of the Rhymes",
            """
           Mach dich bereit für einen Abend voller Punchlines, Flow und harter Konter. 
           Erlebe, wie die MCs Runde für Runde um den Sieg battlen und mit kreativen Bars und spontanen Freestyles das Publikum überzeugen. 
           Ob du wegen der Technik, der Energie oder der Atmosphäre kommst – hier feierst du Battle-Rap so, wie er sein soll: laut, direkt und mit einer Crowd, die jede starke Line spürbar macht.
            """,
            LocalDateTime.of(2026, 12, 1, 20, 0),
            25.0
        );
        rapBattle.addSection(new StandingSection("Innenraum (Stehplatz)", 1.0, 2000));
        rapBattle.addSection(new SeatedSection("Block 1", 1.2, 10, 20));
        rapBattle.addSection(new SeatedSection("Block 2", 1.2, 10, 20));
        rapBattle.addSection(new SeatedSection("Block 3", 1.2, 10, 20));
        rapBattle.addSection(new SeatedSection("Block 4", 1.2, 10, 20));
        rapBattle.addSection(new SeatedSection("Block 6", 0.8, 10, 20));
        rapBattle.addSection(new SeatedSection("VIP", 2.5, 3, 15));
        return rapBattle;
    }

    
      //                                 //
     //Events Nutzung der Innenfläche   // 
    //                                 //

    // Event 3: Sportevent - Innenraum ist Spielfläche
    private static Event createSportEvent() {
        Event sport = new Event(
            3L,
            "Alba Berlin vs. FC Bayern München",
            """
            Im Rahmen der regulären Saison der Basketball-Bundesliga treffen ALBA Berlin und der FC Bayern Basketball aufeinander. 
            Beide Mannschaften zählen seit Jahren zu den erfolgreichsten Teams der Liga und versprechen ein intensives Duell auf hohem sportlichem Niveau. 
            Das Spiel findet in einer modernen Arena mit nummerierten Sitzplätzen und guter Sicht auf das Spielfeld statt.                    
            """,
            LocalDateTime.of(2026, 8, 11, 18, 00),
            60.0
            );
        sport.addSection(new EmptySection("Spielfläche"));
        sport.addSection(new SeatedSection( "Fankurve Heim", 0.9, 20, 30));
        sport.addSection(new SeatedSection( "Gästeblock", 0.9, 10, 20));
        sport.addSection(new SeatedSection( "Haupttribüne", 1.2, 15, 30));
        return sport;
    }

     // Event 8: Tanzwettbewerb - Innenraum ist Tanzfläche
    private static Event createDanceCompetition(){
        Event danceCompetition = new Event(
            8L,
            "Tanzwettbewerb: Dance Masters",
            """
            Erleben Sie die besten Tänzerinnen und Tänzer in einem spannenden Wettbewerb, bei dem verschiedene Tanzstile von klassischem Ballett bis hin zu modernem Streetdance präsentiert werden. 
            Die Teilnehmerinnen und Teilnehmer zeigen ihr Können in beeindruckenden Choreografien und treten gegeneinander an, um den Titel des Dance Masters zu gewinnen.
            """,
            LocalDateTime.of(2026, 11, 5, 18, 30),
            35.0
        );
        danceCompetition.addSection(new EmptySection("Spielfläche"));
        danceCompetition.addSection(new SeatedSection( "Fankurve Heim", 0.9, 20, 30));
        danceCompetition.addSection(new SeatedSection( "Gästeblock", 0.9, 10, 20));
        danceCompetition.addSection(new SeatedSection( "Haupttribüne", 1.2, 15, 30));
        return danceCompetition;
    }


      //                                   //
     //Events Sitzfläche im Innenraum// 
    //                                 //

    // Event 2: Gala mit Innenraum Bestuhlung
    private static Event createGala(){
        Event gala = new Event(
            2L,
            "Klassik Gala",
            """
            Erleben Sie einen festlichen Abend mit einem abwechslungsreichen Programm aus den bekanntesten Werken der klassischen Musik.
            Freuen Sie sich auf herausragende Solistinnen und Solisten, ein renommiertes Orchester sowie eine stilvolle Atmosphäre, 
            die Musikliebhaberinnen und Musikliebhaber gleichermaßen begeistern wird.
                    """,
            LocalDateTime.of(2026, 12, 15, 20, 0),
            80.0
            );
        gala.addSection(new SeatedSection("Parkett (bestuhlter Innenraum)", 1.0, 15, 12));
        gala.addSection(new SeatedSection("Loge", 2.0, 5, 6));
        gala.addSection(new SeatedSection("Premium", 1.5, 10, 8));
        return gala;
    }

    // Event 4: Theaterstück - Innenraum ist Sitzfläche 
    private static Event createTheater() {
        Event theater = new Event(
            4L,
            "Theaterstück: Hamlet",
            """
            Erleben Sie das zeitlose Drama 'Hamlet' von William Shakespeare in einer fesselnden Inszenierung. 
            Tauchen Sie ein in die Welt von Intrigen, Rache und Tragik, während talentierte Schauspielerinnen und Schauspieler die komplexen Charaktere zum Leben erwecken.
            """,
            LocalDateTime.of(2026, 10, 5, 19, 30),
            45.0
            );
        theater.addSection(new SeatedSection( "Parkett",  0.8, 15, 12));
        theater.addSection(new SeatedSection( "Block 1", 1.2, 10, 20));
        theater.addSection(new SeatedSection( "Block 2", 1.2, 10, 20));
        theater.addSection(new SeatedSection( "Block 3", 1.2, 10, 20));
        theater.addSection(new SeatedSection( "Block 4", 1.2, 10, 20));
        theater.addSection(new SeatedSection( "Block 6", 0.8, 10, 20));
        theater.addSection(new SeatedSection("VIP", 2.5, 3, 15));
        return theater;
    }

    // Event 5: Musical - Innenraum ist Sitzfläche
    private static Event createMusical() {
        Event musical = new Event(
            5L,
            "Musical: Starlight Dreams",
            """
            "Starlight Dreams" erzählt die Geschichte einer jungen Musikerin, die ihren Traum von einer Karriere auf der großen Bühne verfolgt. 
            Begleitet wird die Handlung von eigens komponierten Songs, aufwendigen Choreografien und einem abwechslungsreichen Bühnenbild. 
            Das Musical richtet sich an Besucherinnen und Besucher jeden Alters.auchen Sie ein in die faszinierende Welt von Liebe, Leidenschaft und Geheimnissen, während talentierte Darstellerinnen und Darsteller die unvergesslichen Charaktere zum Leben erwecken.
            """,
            LocalDateTime.of(2026, 9, 20, 19, 30),
            50.0
        );
        musical.addSection(new SeatedSection( "Parkett",  0.8, 15, 12));
        musical.addSection(new SeatedSection( "Block 1", 1.2, 10, 20));
        musical.addSection(new SeatedSection( "Block 2", 1.2, 10, 20));
        musical.addSection(new SeatedSection( "Block 3", 1.2, 10, 20));
        musical.addSection(new SeatedSection( "Block 4", 1.2, 10, 20));
        musical.addSection(new SeatedSection( "Block 6", 0.8, 10, 20));
        musical.addSection(new SeatedSection("VIP", 2.5, 3, 15));
        return musical;
    }

    // Event 6: Comedy Show - Innenraum ist Sitzfläche
    private static Event createComedy() {
        Event comedy = new Event(
            6L,
            "Comedy Show: The Laugh Factory",
            """
            Genießen Sie einen Abend voller Humor mit bekannten Comedians und aufstrebenden Nachwuchstalenten. 
            Freuen Sie sich auf schlagfertige Stand-up-Comedy, spontane Improvisationen und unterhaltsame Geschichten aus dem Alltag. 
            Ob feinsinniger Wortwitz oder pointierte Gesellschaftssatire – hier bleibt garantiert kein Auge trocken. 
            """,
            LocalDateTime.of(2026, 8, 15, 20, 0),
            45.0
        );
        comedy.addSection(new SeatedSection( "Parkett",  0.8, 15, 12));
        comedy.addSection(new SeatedSection( "Block 1", 1.2, 10, 20));
        comedy.addSection(new SeatedSection( "Block 2", 1.2, 10, 20));
        comedy.addSection(new SeatedSection( "Block 3", 1.2, 10, 20));
        comedy.addSection(new SeatedSection( "Block 4", 1.2, 10, 20));
        comedy.addSection(new SeatedSection( "Block 6", 0.8, 10, 20));
        comedy.addSection(new SeatedSection("VIP", 2.5, 3, 15));
        return comedy;
    }

    // Event 7: Live Podcast - Innenraum ist Sitzfläche
    private static Event createLivePodcast(){
        Event livePodcast = new Event(
            7L,
            "Live Podcast: TechTalks",
            """
            Erleben Sie die beliebte TechTalks-Podcastreihe live auf der Bühne. 
            Hören Sie spannende Diskussionen über aktuelle Technologietrends, Innovationen und die Zukunft der digitalen Welt. 
            Die Hosts und Gäste teilen ihre Erfahrungen, beantworten Fragen aus dem Publikum und bieten exklusive Einblicke hinter die Kulissen der Tech-Branche.
            """,
            LocalDateTime.of(2026, 10, 10, 19, 0),
            30.0
        );
        livePodcast.addSection(new SeatedSection( "Parkett",  0.8, 15, 12));
        livePodcast.addSection(new SeatedSection( "Block 1", 1.2, 10, 20));
        livePodcast.addSection(new SeatedSection( "Block 2", 1.2, 10, 20));
        livePodcast.addSection(new SeatedSection( "Block 3", 1.2, 10, 20));
        livePodcast.addSection(new SeatedSection( "Block 4", 1.2, 10, 20));
        livePodcast.addSection(new SeatedSection( "Block 6", 0.8, 10, 20));
        livePodcast.addSection(new SeatedSection("VIP", 2.5, 3, 15));
        return livePodcast;
    }

}
