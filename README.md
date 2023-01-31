# ChatGames

ChatGames is a plugin that puts fun games into your Minecraft chat.

## TODO

- ~~Make everything translatable using a message.yml or messages.json~~
  - ~~Mini message with slots for the values that matter~~
- Add more games (I need ideas!)
- More configuration
- Work on `/chatgames` command
  - ~~add `/chatgames next`~~
  - ~~remove `/chatgames random` - was just a test~~
  - ~~add `/chatgames list` to view the word lists (~~paginated or~~ https://pastes.dev/)~~
- ~~Add vault integration~~

## Configuration

In the config you can configure a lot of the core functionality of the plugin:

Word lists (built-in ones and custom ones)
- Built-In: blocks, items, mobs, countries
- Custom lists by adding files
- Check the config file for more information

Reward
- Items
- Commands
- Money (Using Vault)

## PlaceholderAPI Integration

Currently offering two placeholders:

- `%chatgames_current-game%` - The name of the current game (defined in the `messages.yml`)
- `%chatgames_current-word%` - The current word for the current game

## Commands

- `/chatgames reload` - Reload the plugin
- `/chatgames next` - Switch to the next game
- `/chatgames lists` - Upload the word lists to pastes.dev

## Current Games (looking for ideas)

### Fill in the blanks

Fill in the blanks of a word to score the point

### Scramble

Unscramble a word!