package src;
import java.util.*;

public class ChessGame {
    private static final int MAX_DEPTH = 100000;
    private char[][] board;
    private int n;
    private int m;


    private boolean isFirst;
    ChessStream chessStream = new ChessStream();

    HashMap<Long, Integer> transpositionTable = new HashMap<Long, Integer>();
    // 3D array for storing Zobrist numbers
    private long[][][] zobristTable;

    public ChessGame(int n, int m, int order) {
        this.n = n;
        this.m = m;
        this.board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = ' '; // Initialize the board with empty spaces
            }
        }
        initializeZobristTable();
        if (order == 1) {
            isFirst = true;
        } else if (order == 2) {
            isFirst = false;
        } else {
            System.out.println("Error input order, please run again.");
            System.exit(0);
        }

        // Initialize Zobrist table

    }

    private void initializeZobristTable() {
        zobristTable = new long[n][n][3]; // n x n x 3 for 'X', 'O', and ' '
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < 3; k++) {
                    zobristTable[i][j][k] = rand.nextLong();
                }
            }
        }
    }

    public boolean boardFull() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public void playGame(int order) {
        System.out.println("type the input path");
        String inputPath = chessStream.GetPath(); // Renamed from GetPath to getPath
        System.out.println("type the output path");
        String outputPath = chessStream.GetPath(); // Renamed from GetPath to getPath
        if(order == 1){
            firstPlay(inputPath, outputPath);
        }
        if(order == 2) {
            secondPlay(inputPath, outputPath);
        }
    }

    private void secondPlay(String inputPath, String outputPath) {
        while (!boardFull() && !checkWin('X') && !checkWin('O')) {
            // Our Second
            while (true){
                if (checkWin('X')) {
                    printBoard();
                    System.out.println("Our lose.");
                    break;
                }
                if (boardFull()){
                    System.out.println("The game is a draw.");
                    break;
                }
                System.out.println("Scan opposite file from " + inputPath);

                boolean validMove = false;
                while (!validMove) {
                    List<Integer> xy = chessStream.ReadTimer(inputPath).getXY();
                    int row = xy.get(1);
                    int col = xy.get(0);
                    System.out.println("opposite: is " + col +" "+row +" Our computer are thinking.........");
                    if (makeMove(row - 1, col - 1, 'X')) {
                        validMove = true;
                    } else {
                        System.out.println("Invalid move, try again.");
                        secondPlay(inputPath,outputPath);
                    }
                }

                int[] move = findBestMove();
                makeMove(move[0],move[1],'O');
                System.out.println("Computer moved to " + (move[1]+1) + " " + (move[0]+1));
                printBoard();
                if (boardFull()){
                    System.out.println("The game is a draw.");
                    break;
                }
                chessStream.WriteMyMove(move[1]+1,move[0]+1,outputPath);
                if (checkWin('O')) {
                    printBoard();
                    System.out.println("Our wins!");
                    break;
                }
            }
        }
    }


    private void firstPlay(String inputPath, String outputPath) {
        while (!boardFull() && !checkWin('X') && !checkWin('O')) {
            while (true) {
                System.out.println("Our Computer is making a move...");
                int[] move = findBestMove();
                makeMove(move[0], move[1], 'O');
                System.out.println("Computer moved to " + (move[1]+1) + " " + (move[0]+1));
                printBoard();
                if (boardFull()) {
                    System.out.println("The game is a draw.");
                    chessStream.WriteMyMove(move[1]+1, move[0]+1, inputPath);
                    System.exit(0);

                }
                chessStream.WriteMyMove(move[1]+1, move[0]+1, inputPath);
                if (checkWin('O')) {
                    printBoard();
                    System.out.println("Our wins!");
                    break;
                }

                boolean validMove = false;
                while (!validMove) {
                    List<Integer> xy = chessStream.ReadTimer(outputPath).getXY();
                    int row = xy.get(1);
                    int col = xy.get(0);
                    if (makeMove(row - 1, col - 1, 'X')) {
                        validMove = true;
                    } else {
                        System.out.println("Invalid move, try again.");
                        firstPlay(inputPath,outputPath);
                    }
                }

                if (checkWin('X')) {
                    printBoard();
                    System.out.println("Our lose.");
                    break;
                }
                if (boardFull()){
                    System.out.println("The game is a draw.");
                    break;
                }
            }
        }
    }



    private void printBoard() {
        // Print column numbers
        System.out.print("   ");
        for (int i = 1; i <= n; i++) {
            if (i <= 9) {
                System.out.print("0" + i + "  ");
            } else {
                System.out.print(i + "  ");
            }
        }
        System.out.println();

        // Print the board rows with separators
        for (int i = 0; i < n; i++) {
            if (i + 1 <= 9) {
                System.out.print("0" + (i + 1));
            } else {
                System.out.print(i + 1);
            }

            // Print row cells with vertical separators
            for (int j = 0; j < n; j++) {
                System.out.print("| " + board[i][j] + " ");
            }
            System.out.println("|");

            // Print horizontal row separator
            if (i < n - 1) {
                System.out.print("  ");
                for (int j = 0; j < n; j++) {
                    System.out.print("----");
                }
                System.out.println("-");
            }
        }
        System.out.println();
    }


    private boolean makeMove(int row, int col, char player ) {
        // Check if the move is within the board

        if (row < 0 || row > n || col < 0 || col > n) {
            System.out.println("Invalid move. Position is outside the board.");
            return false;
        }

        // Check if the specified cell is empty
        if (board[row][col] != ' ') {
            System.out.println("Invalid move. The cell is already occupied.");
            return false;
        }

        if (boardFull()) {
            System.out.println("It is draw");
            return false;
        }

        // Make the move
        board[row][col] = player;
        return true;
    }


    int[] findBestMove() {

        int depth = 1; // Start with a depth of 1
        int[] bestMove = new int[2];

        long timeLimit = 8000; // Set a time limit of 8 seconds
        long startTime = System.currentTimeMillis(); // Record the start time

        while (true) {
            int currentBestScore = Integer.MIN_VALUE;
            int[] currentBestMove = new int[2];

            // Create a list of all possible moves
            List<int[]> moves = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == ' ') {
                        moves.add(new int[] {i, j});
                    }
                }
            }

            // Order the moves using some heuristic
            Collections.sort(moves, new Comparator<int[]>() {
                @Override
                public int compare(int[] move1, int[] move2) {
                    // Check if the positions resulting from move1 and move2 are in the transposition table
                    board[move1[0]][move1[1]] = 'O';
                    long hash1 = generateBoardHash();
                    board[move1[0]][move1[1]] = ' ';

                    board[move2[0]][move2[1]] = 'O';
                    long hash2 = generateBoardHash();
                    board[move2[0]][move2[1]] = ' ';

                    boolean inTable1 = transpositionTable.containsKey(hash1);
                    boolean inTable2 = transpositionTable.containsKey(hash2);

                    // Prioritize moves that are in the transposition table
                    if (inTable1 && !inTable2) {
                        return -1;
                    } else if (!inTable1 && inTable2) {
                        return 1;
                    }

                    // If both or neither are in the transposition table, use another criterion
                    // For example, distance to the center
                    int dist1 = Math.abs(n/2 - move1[0]) + Math.abs(n/2 - move1[1]);
                    int dist2 = Math.abs(n/2 - move2[0]) + Math.abs(n/2 - move2[1]);
                    return dist1 - dist2;
                }
            });

            // Iterate over the ordered moves
            for (int[] move : moves) {
                board[move[0]][move[1]] = 'O';
                int score = minimax(depth, 'X', Integer.MIN_VALUE, Integer.MAX_VALUE);
                board[move[0]][move[1]] = ' ';
                if (score > currentBestScore) {
                    currentBestScore = score;
                    currentBestMove = move;
                }
            }

            // Check if the time limit has been exceeded after evaluating all moves
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > timeLimit) {
                return bestMove;
            }

            // If we have a winning move, or we have reached the maximum depth, stop
            if (currentBestScore == Integer.MAX_VALUE || depth == MAX_DEPTH) {
                return currentBestMove;
            }

            // Update the best move found so far
            bestMove = currentBestMove;
            // Increase depth for the next iteration
            depth++;
        }
    }







    private int evaluateBoard() {
        int score = 0;

        // You can create a 4D array to easily check all possible lines of length m
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (board[x][y] == ' ') {
                    for (int[] d : directions) {
                        score += evaluateLine(x, y, d[0], d[1]);
                    }
                }
            }
        }
        return score;
    }



    private int evaluateLine(int x, int y, int dx, int dy) {
        int humanPoints = 0, aiPoints = 0;
        int gapCount = 0;
        boolean inSequence = false;

        for (int i = 0; i < m; i++) {
            if (isInsideBoard(x + dx * i, y + dy * i)) {
                if (board[x + dx * i][y + dy * i] == 'O') {
                    if (inSequence) {
                        // If we're in the middle of a sequence and we encounter another 'O', reset the gap count
                        gapCount = 0;
                    } else {
                        // If we're not in a sequence and we encounter an 'O', start a new sequence
                        inSequence = true;
                    }


                    aiPoints++;
                } else if (inSequence && board[x + dx * i][y + dy * i] == ' ') {
                    // If we're in a sequence and we encounter an empty space, increment the gap count
                    gapCount++;
                } else if (board[x + dx * i][y + dy * i] == 'X') {
                    // If we encounter an 'X', end the current sequence
                    inSequence = false;
                }

                if (board[x + dx * i][y + dy * i] == 'X') humanPoints++;
            }
        }

        // Evaluations for 5 in a row
        if (aiPoints == m) return Integer.MAX_VALUE; // AI has five in a row
        if (humanPoints == m) return Integer.MIN_VALUE; // Human has five in a row

        // Evaluations for 4 in a row
        if (aiPoints == m-1 && humanPoints == 0) return 15000; // AI has a live four or dead four
        if (humanPoints == m-1 && aiPoints == 0) return 15000; // Human has a live four or dead four

        // Evaluations for 3 in a row
        if (aiPoints == m-2 && humanPoints == 0) return 10000 ; // AI has a potential trap
        if (humanPoints == m-2 && aiPoints == 0) return -8000; // Human has a potential trap

        // Evaluations for disrupted lines
        if (aiPoints == m-1 && humanPoints == 1) return 7000; // AI has four in a row but disrupted by human
        if (humanPoints == m-1 && aiPoints == 1) return -5000; // Human has four in a row but disrupted by AI
        if (aiPoints == m-2 && humanPoints == 1) return 4000; // AI has three in a row but disrupted by human
        if (humanPoints == m-2 && aiPoints == 1) return -2000; // Human has three in a row but disrupted by AI

        // Scenarios with only one type of pieces
        if (aiPoints == 1 && humanPoints == 0) return 100;
        if (humanPoints == 1 && aiPoints == 0) return -200;
        if (aiPoints == 2 && humanPoints == 0) return 600;
        if (humanPoints == 2 && aiPoints == 0) return -400;

        if(m>8) {
            if (aiPoints == 4 && humanPoints == 0) return 2000;
            if (humanPoints == 4 && aiPoints == 0) return -1600;
            if (aiPoints == 5 && humanPoints == 0) return 4000;
            if (humanPoints == 5 && aiPoints == 0) return -3200;
            if (aiPoints == 6 && humanPoints == 0) return 8000;
            if (humanPoints == 6 && aiPoints == 0) return -6400;
        }
        if(m<=6) {
            // "Jumped" threes for the AI
            if (aiPoints == 3 && gapCount == 1) return 2000;
            if (humanPoints == 3 && gapCount == 1) return -1600;
            if (aiPoints == 4 && gapCount == 1) return 4000;
            if (humanPoints == 4 && gapCount == 1) return -3200;
        }

        // Default case: evaluate based on difference of AI and human points, considering if line is open
        int aiDefault =aiPoints * 10 ;
        int humanDefault = humanPoints -10 ;

        return aiDefault + humanDefault;
    }








    private boolean isInsideBoard(int x, int y){
        return x >= 0 && x < n && y >= 0 && y < n;
    }


    private int minimax(int depth, char player, int alpha, int beta) {
        long boardHash = generateBoardHash();
        if (transpositionTable.containsKey(boardHash)) {
            return transpositionTable.get(boardHash);
        }

        if (checkWin('O')) {
            transpositionTable.put(boardHash, Integer.MAX_VALUE);
            return Integer.MAX_VALUE;
        } else if (checkWin('X')) {
            transpositionTable.put(boardHash, Integer.MIN_VALUE);
            return Integer.MIN_VALUE;
        } else if (boardFull() || depth == 0) {
            int score = evaluateBoard();
            transpositionTable.put(boardHash, score);
            return score;
        }

        if (player == 'O') {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = player;
                        int eval = minimax(depth-1, 'X', alpha, beta);
                        board[i][j] = ' ';
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            transpositionTable.put(boardHash, maxEval);
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = player;
                        int eval = minimax(depth-1, 'O', alpha, beta);
                        board[i][j] = ' ';
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            transpositionTable.put(boardHash, minEval);
            return minEval;
        }
    }



    private long generateBoardHash() {
        long hash = 0L;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'X') {
                    hash ^= zobristTable[i][j][0];
                } else if (board[i][j] == 'O') {
                    hash ^= zobristTable[i][j][1];
                } else { // ' '
                    hash ^= zobristTable[i][j][2];
                }
            }
        }
        return hash;
    }


    private boolean checkWin(char player) {
        // Implement this method to check if the game is won
        //check horizontal line
        for (int i = 0; i < n ; i++) {
            for(int j = 0;j<n-m+1;j++) {
                int k;
                for(k = 0; k < m; k++) {
                    if(board[i][j+k] != player)
                        break;
                }
                if (k == m)
                    return true;
            }
        }

        //check vertical line
        for (int i = 0; i < n-m+1 ; i++) {
            for(int j = 0;j<n;j++) {
                int k;
                for(k = 0; k < m; k++) {
                    if(board[i+k][j] != player)
                        break;
                }
                if (k == m)
                    return true;
            }
        }

        //check diagonals line from top right to bottom left
        for(int i=0; i<n-m+1;i++) {
            for(int j = 0; j<n-m+1; j++) {
                boolean diagonalsignals = true;
                boolean notdiagonals = true;

                for(int k = 0; k < m; k++) {
                    if(board[i+k][j+k] != player) {
                        diagonalsignals = false;
                    }
                    if(board[i+k][j+m-1-k] != player){
                        notdiagonals = false;
                    }
                }
                if (diagonalsignals || notdiagonals) {
                    return true;
                }
                //check diagonals line from top left to bottom right

                // replace this
            }
        }
        return false; }


    /*private static boolean checkSign(int a){
        if(a==1){return false;}
        else if(a == 2){return true;}
        return true;
    }*/
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the size of the board: ");
        int n = scanner.nextInt();
        System.out.println("Enter the number of pieces connected to win: ");
        int m = scanner.nextInt();
        System.out.println("Who moves first? 1 Our first, 2 Our second");
        int firstMove = scanner.nextInt();

        ChessGame game = new ChessGame(n, m, firstMove);
        game.initializeZobristTable();
        game.playGame(firstMove);
    }
}
