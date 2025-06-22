# OpenNicks

OpenNicks is a Minecraft plugin for Paper 1.21+ that allows players to set and manage their own nicknames in-game. Nicknames are stored persistently and can be changed or removed at any time.

## Features

- Set a custom nickname using `/nick <name>`
- Remove your nickname with `/nick off`
- Nicknames are stored in a database and cached for performance
- Prevents duplicate nicknames
- Simple permission system

## Commands

- `/nick <name>` — Set your nickname
- `/nick off` — Remove your nickname

## Permissions

- `opennicks.nickname` — Allows the use of the `/nick` command

## TO-DO
- Add support for nickname history
- Implement nickname cooldowns
- Create a GUI for nickname management
- Add a administration command to manage nicknames of other players
