# connect4
Simple alpha-beta minmax algorithm for the "connect 4" game

## Features
- Complete minmax algorithm
- Complete and working alpha-beta algorithm
- Some performance improvements like better isWinning method
- Dynamic Depth (Scaling up as rounds go on, Early game -> significantly less depth, Late game -> On depth specified)
- Move ordering for better beta cutoffs
- Codebase is well documented

This connect4 bot is quite fast and I managed a good experience (-> not long wait times) with a search depth of 20. This is on a quite powerful pc, but still. I think my algorithm is optimized enough to work on any machine with search depth > 12. I managed to achieve the best results with a search depth of 16, before that the game wouldn't even end in a draw.

**If I had to make a mark, I would give myself a 6**
