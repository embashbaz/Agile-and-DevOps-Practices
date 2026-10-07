# Note taking app backend

## Product vision

An api first app  that anyonce can use to to keep track of their notes and access them through an api.
This is a self hosted solution allowing for privacy.

##  User stories

- As a user i want to be able to create an account so that i can securely use the app.
    Acceptance criteria: 
    - Given a user account doesn't exist when a user provide an email and password, then they should get a response saying that their account has been created.
    - Given a account exist with an email, when someone tries to create an account with the email, then they should get an error <br>

    Prioritization: Must have.<br>
    Estimation: 8
- As a user i want to be able to login in my account so that i can securely use the app.
   Acceptance criteria: 
   - Given a user account exist when a user provide the correct email and password that already exist on the system, then they should get a response saying that they have been loggedin.
    - Given a user account exist when a user provide the wrong email and password, then they should get an error.
    - given an account doesn't exist, when a user tries to login, then they should be shown an error saying that their account doesn't exist<br>

   Prioritization: Must have.<br>
    Estimation: 3
- As a user i want to be able to add a note that that i can get back to it later.
    Acceptance criteria:
    - given wwhen a user provide a valid user id and a note in a specific post request, they should get a response that the note has been save.
    - given a user provide an invalid user id when they try to add a new note, then they should get an error saying that the user id is invalid.<br>

    Prioritization: Must have.<br>
    Estimation: 5
- As a user I want to be able to get all my notes so that i can get information from them.
    Acceptance criteria: 
    - when a user provide their user id for a specific get request, they should get a response with all their notes.
    - given a user provide an invalid user id when they try to get notes, then they should get an error saying that the user id is invalid.<br>

    Prioritization: Must have.<br>
    Estimation: 2
- As a use i want to be anle to get a singe note so that i can get information from it.
    Acceptance criteria: 
    - a user provide a specic note id as param for a specic get request, they should get a response with the text content of that note.
    -  given a user provide an invalid user id when they try to get a note, then they should get an error saying that the user id is invalid.
    -  given a user provide an invalid note id when they try to get a note, then they should get an error saying that the note id is invalid.<br>

    Prioritization: Should have.<br>
    Estimation: 2

## Definition of Done

A feature is done when the following are true
- Acceptance criteria have been met
- unit tests have been written and passed
- code has been commited and pushed to the repository
- Code passes all checks in the CI pipeline.

## Sprint planning

### Sprint 1

- As a user i want to be able to create an account so that i can securely use the app.
- As a user i want to be able to login in my account so that i can securely use the app.


