package domain;

/** Definiert die zulässigen Kundengruppen für Preis- und Rabattregeln. */
public enum CustomerType {
    /** Regulärer Kunde ohne Ermäßigung. */
    STANDARD,
    /** Studierende mit dem im Buchungsservice definierten Rabatt. */
    STUDENT,
    /** Seniorinnen und Senioren mit Ermäßigung. */
    SENIOR,
    /** Kunde mit VIP-Klassifizierung. */
    VIP,
    /** Kind mit altersbezogener Ermäßigung. */
    KIND
}

