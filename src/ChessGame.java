package src;
import java.util.List;
import java.util.Scanner;

public class ChessGame {
    private static final int MAX_DEPTH = 10;
    private char[][] board;
    private int n;
    private int m;

    private boolean isFirst; // Renamed from IsFirst to isFirst
    ChessSteam chessSteam = new ChessSteam();

    public ChessGame(int n, int m, int order) {

        if(order == 1){
            isFirst = true;
        }
        else if(order == 2){
            isFirst = false;
        }
        else {
            System.out.println("error input order, please run again");
            System.exit(0);
        }

        this.n = n;
        this.m = m;
        this.board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = ' '; // Initialize the board with empty spaces
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
        String inputPath = chessSteam.GetPath(); // Renamed from GetPath to getPath
        System.out.println("type the output path");
        String outputPath = chessSteam.GetPath(); // Renamed from GetPath to getPath
        System.out.println("order is " + order);
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

                List<Integer> xy = chessSteam.ReadTimer(inputPath).getXY();
                int row = xy.get(1);
                int col = xy.get(0);
                System.out.println("opposite: is " + col +" "+row +" Our computer are thinking.........");
                if (!makeMove(row - 1, col - 1, 'X') ) { // Player is represented by 'X'
                    System.out.println("Invalid move, try again.");
                    secondPlay(inputPath,outputPath);
                }
                int[] move = findBestMove();
                makeMove(move[0],move[1],'O');
                System.out.println("Computer moved to " + (move[1]+1) + " " + (move[0]+1));
                printBoard();
                if (boardFull()){
                    System.out.println("The game is a draw.");
                    break;
                }
                chessSteam.WriteMyMove(move[1]+1,move[0]+1,outputPath);
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

            while(true){

                System.out.println("Our Computer is making a move...");
                int[] move = findBestMove();
                makeMove(move[0], move[1], 'O');
                System.out.println("Computer moved to " + (move[1]+1) + " " + (move[0]+1));
                printBoard();
                if (boardFull()){
                    System.out.println("The game is a draw.");
                    chessSteam.WriteMyMove(move[1]+1, move[0]+1, inputPath);
                    break;
                }
                chessSteam.WriteMyMove(move[1]+1, move[0]+1, inputPath);
                if (checkWin('X')) {
                    printBoard();
                    System.out.println("Our lose.");
                    break;
                }
                List<Integer> xy = chessSteam.ReadTimer(outputPath).getXY();
                if (checkWin('X')) {
                    printBoard();
                    System.out.println("Our lose.");
                    break;
                }
                if (boardFull()){
                    System.out.println("The game is a draw.");
                    break;
                }
                int row = xy.get(1);
                int col = xy.get(0);
                if (!makeMove(row - 1, col - 1, 'X')) {
                    System.out.println("Invalid move, try again.");
                    firstPlay(inputPath,outputPath);
                }
                if (checkWin('O')) {
                    printBoard();
                    System.out.println("Our wins!");
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
        if (boardFull()) {
            throw new IllegalStateException("The board is full. No valid moves are available.");
        }

        int depth = 1; // Start with a depth of 1
        int[] bestMove = new int[2];

        long startTime = System.currentTimeMillis(); // Record the start time
        long timeLimit = 5000; // Set a time limit of 5000 milliseconds (5 seconds)

        outerloop:
        while (true) { // Keep increasing depth until a stopping condition is met
            int currentBestScore = Integer.MIN_VALUE;
            int[] currentBestMove = new int[2];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O';
                        int score = minimax(depth, 'X', Integer.MIN_VALUE, Integer.MAX_VALUE);
                        board[i][j] = ' ';
                        if (score > currentBestScore) {
                            currentBestScore = score;
                            currentBestMove[0] = i;
                            currentBestMove[1] = j;
                        }
                    }
                    long elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
                    // If elapsed time is greater than the time limit, stop and return the best move found so far
                    if (elapsedTime > timeLimit) {
                        break outerloop;
                    }
                }
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
        return bestMove;
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
        int humanPieces = 0, aiPieces = 0;
        for (int i = 0; i < m; i++) {
            if (isInsideBoard(x + dx * i, y + dy * i)) {
                if (board[x + dx * i][y + dy * i] == 'X') humanPoints++;
                if (board[x + dx * i][y + dy * i] == 'O') aiPoints++;
            }

            if ((x + dx) >= 0 && (x + dx) < n && (y + dy) >= 0 && (y + dy) < n && board[x + dx][y + dy] == ' ') {
                humanPieces += 50;
            }

            if ((x - dx) >= 0 && (x - dx) < n && (y - dy) >= 0 && (y - dy) < n && board[x - dx][y - dy] == ' ') {
                humanPieces += 50;
            }

            if ((x + m*dx) >= 0 && (x + m*dx) < n && (y + m*dy) >= 0 && (y + m*dy) < n && board[x + m*dx][y + m*dy] == ' ') {
                aiPieces += 100;
            }

            if ((x - m*dx) >= 0 && (x - m*dx) < n && (y - m*dy) >= 0 && (y - m*dy) < n && board[x - m*dx][y - m*dy] == ' ') {
                aiPieces += 100;
            }

        }

        if (aiPoints == m) return Integer.MIN_VALUE; // AI has five in a row
        if (humanPoints == m) return Integer.MAX_VALUE; // Human has five in a row
        if (humanPoints == m-1 && aiPoints == 0) return -20000; // Human has a live four
        if (aiPoints == m-1 && humanPoints == 0) return 20000; //
        if (aiPoints == m-1 && humanPoints == 1) return 10000; //
        if (humanPoints == m-1 && aiPoints == 1) return -8000;
        if (aiPoints == m-2 && humanPoints == 1) return 7000; //
        if (humanPoints == m-2 && aiPoints == 1) return -5000;
        if (aiPoints == m-2 && humanPoints == 0) return 4500; //
        if (humanPoints == m-2 && aiPoints == 0) return -3000;
        if (aiPoints == m-3 && humanPoints == 0) return 2500; //
        if (humanPoints == m-3 && aiPoints == 0) return -2000;

        // AI has a live four
        if(humanPoints == 1) return -100; //with no barrier
        if(aiPoints == 1) return 100; //with no barrier
        if(humanPoints == 2&& aiPoints == 0) return -1000; //with no barrier
        if(aiPoints == 2 && humanPoints == 0) return 2000; //with no barrier
        if(m>5){
            if(humanPoints == 3&& aiPoints == 0) return -3000; //with no barrier
            if(aiPoints == 3 && humanPoints == 0) return 4000; //with no barrier
        }
        return aiPieces - humanPieces;
    }




    private boolean isInsideBoard(int x, int y){
        return x >= 0 && x < n && y >= 0 && y < n;
    }


    private int minimax(int depth, char player, int alpha, int beta) {
        // Base case - check if win or loss or draw, then return score
        if (checkWin('O')) { return Integer.MAX_VALUE; }
        else if (checkWin('X')) { return Integer.MIN_VALUE; }
        else if (boardFull() || depth == 0) { return evaluateBoard(); } // depth == 0 is the base case to stop recursion
        if (player == 'O') {
            int maxEval = Integer.MIN_VALUE;
            // loop through all possible moves
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Check if spot is empty
                    if (board[i][j] == ' ') {
                        // Make move
                        board[i][j] = player;
                        int eval = minimax(depth-1, 'X', alpha, beta); // depth is decreased by 1 here
                        // Undo move
                        board[i][j] = ' ';
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha)
                            break;
                    }
                }
            }
            return maxEval;
        } else { // If player is X
            int minEval = Integer.MAX_VALUE;
            // loop through all possible moves
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Check if spot is empty
                    if (board[i][j] == ' ') {
                        // Make move
                        board[i][j] = player;
                        int eval = minimax(depth-1, 'O', alpha, beta); // depth is decreased by 1 here
                        // Undo move
                        board[i][j] = ' ';
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha)
                            break;
                    }
                }
            }
            return minEval;
        }
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
        game.playGame(firstMove);
    }
}
