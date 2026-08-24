# ExposedBackend

This is the backend repository for the social media–based game **"Exposed"**, inspired by the game "WhoLiked?".
It operates drastically differently at its core than "WhoLiked?", as the data is **not** fetched by logging into social media accounts
but instead expects **the user to provide the data themselves from the ZIP files provided by the supported social media platforms**.

## Supported Social Media Platforms
TikTok and Instagram

## Data Sensitivity Disclaimer
This app is/can be very invasive with your data, and you should be aware of this before using it.
When uploading, you can consent to which data you want to be parsed and stored.
Alternatively, if you are hesitant about uploading it in the first place, see the bottom section.
The backend can extract the following from your data and store it in a database:
* **Posts and Videos** (TikToks, Reels and Instagram Posts) you've **liked, saved and/or reposted**
* **Comments** you've posted on each platform
* **Terms you've searched** on each platform
* **Content** (limited to TikToks and Reels) you've shared with your **friends** (Disclaimer: this is only displayed when both parties of the chat are in the same room)

## User Consent Disclaimer
All data the app stores and processes is willingly provided by the user and is never retrieved without the user's consent.
By uploading, you agree to the extraction of the data mentioned in the **Data Sensitivity Disclaimer**.

## Features
Players can select N rounds to be played. In the lobby, the data to be displayed can be selected. Each individual feature can be turned off as desired.

Depending on the selected features, the content will be displayed and each user will need to guess which user the content belongs to.
The only exception is when a **Shared Content** round occurs. In these rounds, each player will need to select **which** person sent that content to **whom** (Disclaimer: as previously mentioned, this is limited to content where both parties are in the room).

## Internal Data Handling

The app parses your uploaded ZIP files and extracts the relevant **JSON** files containing your data.
When uploading, the platform you are uploading your data for is declared upfront. All previous entries are considered stale, and are deleted and replaced with the data you just provided.
After extraction, the ZIP file is discarded.
All data is stored in a database so that you don't have to upload your data for every round you play.
You can delete all stored data belonging to your account at any time, at your convenience.

## Tech Stack
* Uses **Spring Boot** for the backend
* Uses **React Native** for the frontend (see: [ExposedAppFrontend](https://github.com/Semmelzahntiger/ExposedApp))
* Uses **PostgreSQL** to store your data

## Setup for Self-Hosting
Provide a `.env` file containing the following:
* **JWT_SECRET**: For secure authentication
* **DB_URL**: The address of your PostgreSQL database
* **DB_USER**: The username of your PostgreSQL user
* **DB_PASSWORD**: The password to log into your PostgreSQL database

## Data Transfer Security Guide
You can cut your own data out of the ZIP files if you want; the parser will still work properly.
Do note, however, that every upload **fully replaces** your previous uploads for that social media platform.
