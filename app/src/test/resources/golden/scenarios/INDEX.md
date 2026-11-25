# Golden game scenarios

Generated from GameScenarios.kt. Each folder holds game.json (input, portable format),
report.txt (all derived values), export.csv and export.txt.

- new_game: Freshly created game: one ongoing round, nothing scored.
- self_picks: Each initial seat wins once by self pick, then an ongoing round.
- all_discard_pairs: The 12 possible winner/discarder combinations with growing hand values. Ends the game at 12 rounds.
- penalties: Divided and non divided penalties mixed with hands, draws and an ongoing round.
- sixteen_draws: A complete game where every hand is a draw.
- full_game: A realistic complete 16 round tournament game with every kind of round.
- tie_all_four: All four players end with the same score.
- tie_first_three: Three players tie for first place (penalties only).
- tie_last_three: Three players tie for last place.
- tie_first_two: Two players tie for first place, the rest are different.
- tie_second_two: Second and third places are tied.
- tie_last_two: Third and fourth places are tied.
- tie_two_pairs: First two tied and last two tied.
- special_names: Names with quotes, commas, accents, emoji and file-name-unsafe characters.
- empty_game_name: Game without a name: exports fall back to the start date.
- best_hand_tie: Two hands share the highest value: the first one is the best hand.
- penalties_only: No hand won yet, only penalties on the ongoing round.
- big_scores: Very large hands to check sign handling and table diffs with big numbers.
- random_01: Seeded random game #1 with 1 rounds.
- random_02: Seeded random game #2 with 2 rounds.
- random_03: Seeded random game #3 with 3 rounds.
- random_04: Seeded random game #4 with 4 rounds.
- random_05: Seeded random game #5 with 5 rounds.
- random_06: Seeded random game #6 with 6 rounds.
- random_07: Seeded random game #7 with 7 rounds.
- random_08: Seeded random game #8 with 8 rounds.
- random_09: Seeded random game #9 with 9 rounds.
- random_10: Seeded random game #10 with 10 rounds.
- random_11: Seeded random game #11 with 11 rounds.
- random_12: Seeded random game #12 with 12 rounds.
- random_13: Seeded random game #13 with 13 rounds.
- random_14: Seeded random game #14 with 14 rounds.
- random_15: Seeded random game #15 with 15 rounds.
- random_16: Seeded random game #16 with 16 rounds.
- random_17: Seeded random game #17 with 1 rounds.
- random_18: Seeded random game #18 with 2 rounds.
- random_19: Seeded random game #19 with 3 rounds.
- random_20: Seeded random game #20 with 4 rounds.
