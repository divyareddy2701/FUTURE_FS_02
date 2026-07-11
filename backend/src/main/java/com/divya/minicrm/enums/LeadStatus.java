package com.divya.minicrm.enums;

/**
 * Represents the current stage of a client lead in the pipeline.
 * NEW        -> lead just came in from a contact form / referral, not yet contacted
 * CONTACTED  -> someone has reached out to the lead at least once
 * CONVERTED  -> the lead became a paying client
 * LOST       -> the lead is no longer active (declined, unresponsive, etc.)
 */
public enum LeadStatus {
    NEW,
    CONTACTED,
    CONVERTED,
    LOST
}
