# 4. Match user based on multiple verified and current surnames

Date: 2017-12-18

## Status

Accepted

## Context

Matching service request can have more than one surname which are current and historical surnames. In the process of 
matching users, the first step is to match users based on surname and date of birth. Since we had list of surnames for a
user we had to decide based on which surname to match user.

## Decision

We decided to match users based on all their current and verified surnames since we were made aware that a user 
can have multiple current and verified surnames

## Consequences

We send match response when user surname matches any one of this current and verified surname 
in the database (in addition to other checks)