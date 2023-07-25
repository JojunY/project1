package src;
import java.util.Random;
import java.util.Scanner;

public class ChessGame {
    private char[][] board;
    private int n;
    private int m;
    private boolean playerTurn;

    public ChessGame(int n, int m, boolean playerTurn) {
        this.n = n;
        this.m = m;
        this.board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = ' '; // Initialize the board with empty spaces
            }
        }
        this.playerTurn = playerTurn;
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


    public void playGame() {
        Scanner scanner = new Scanner(System.in);

        while (!boardFull() && !checkWin('X') && !checkWin('O')) {
            if (playerTurn) {
                printBoard();
                System.out.println("Enter your move (row column): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                while (!makeMove(row - 1, col - 1, 'X')) { // Player is represented by 'X'
                    System.out.println("Invalid move, try again.");
                    System.out.println("Enter your move (row column): ");
                    row = scanner.nextInt();
                    col = scanner.nextInt();
                }
                if (checkWin('X')) {
                    printBoard();
                    System.out.println("Player X wins!");
                    break;
                }
            } else {
                System.out.println("Computer is making a move...");
                int[] move = findBestMove();
                makeMove(move[0], move[1], 'O'); // Computer is represented by 'O'
                System.out.println("Computer moved to " + (move[0] + 1) + " " + (move[1] + 1));
                if (checkWin('O')) {
                    printBoard();
                    System.out.println("Computer wins!");
                    break;
                }
            }

            playerTurn = !playerTurn; // Switch the turn
        }

        if (boardFull() && !checkWin('X') && !checkWin('O')) {
            printBoard();
            System.out.println("The game is a draw.");
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


    private boolean makeMove(int row, int col, char player) {
        // Check if the move is within the board

        if (row < 0 || row > n || col < 0 || col >= n) {
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
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = 'O';
                    int score = minimax(1, 'X', Integer.MIN_VALUE, Integer.MAX_VALUE); // depth is set to 10 here
                    board[i][j] = ' ';
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
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
        for (int i = 0; i < m; i++) {
            if (isInsideBoard(x + dx * i, y + dy * i)) {
                if (board[x + dx * i][y + dy * i] == 'X') humanPoints++;
                if (board[x + dx * i][y + dy * i] == 'O') aiPoints++;
            }
        }

        if (aiPoints == m) return 10000; // AI has five in a row
        if (humanPoints == m) return -10000; // Human has five in a row
        if (m > 3) {
            if (humanPoints == m-1 && aiPoints == 0) return -9050; // Human has a live four
            if (aiPoints == m-1 && humanPoints == 0) return 9030; // AI has a live four
            if (aiPoints == m-1 && humanPoints == 1) return 9010; // AI has a live four
            if (humanPoints == m-1 && aiPoints == 1) return -9020;
            if (aiPoints == m-2 && humanPoints == 1) return 8980; // AI has a live four
            if (humanPoints == m-2 && aiPoints == 1) return -8990;
            if (aiPoints == m-2 && humanPoints == 0) return 9000; // AI has a live four
            if (humanPoints == m-2 && aiPoints == 0) return -9010;
        }

        // AI has a live four
        if(aiPoints == 1) aiPoints += 50; //with no barrier
        if(humanPoints == 2) humanPoints += 200; //with no barrier
        if(aiPoints == 2) aiPoints += 150; //with no barrier
        if(humanPoints == 1) humanPoints += 100; //with no barrier
        if(aiPoints == 3) aiPoints += 250; //with no barrier
        if(humanPoints == 3) humanPoints += 300; //with no barrier

        return aiPoints - humanPoints;
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
        System.out.println("Who moves first? 1 for player, 2 for computer: ");
        int firstMove = scanner.nextInt();
        //checkSign(firstMove);
        ChessGame game = new ChessGame(n, m, firstMove == 1);
        game.playGame();
    }
}
