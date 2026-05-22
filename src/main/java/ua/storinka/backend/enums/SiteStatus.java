package ua.storinka.backend.enums;

public enum SiteStatus {
    /** Created but not yet paid. Frontend routes the owner to the payment step. */
    DRAFT,
    /** Paid and publicly served at subdomain.storinka.com. */
    ACTIVE,
    /** Owner voluntarily paused. Subscription still valid — can resume without new payment. */
    SUSPENDED,
    /** Subscription expired or admin-disabled. Owner must pay to revive. */
    INACTIVE
}
