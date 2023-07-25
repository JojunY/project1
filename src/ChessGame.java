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
        this.board = new char[n+1][n];
        for (int i = 0; i < n+1; i++) {
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
        while (true) {
            printBoard();
            char currentPlayer;
            if (playerTurn) {
                System.out.println("Enter your move (row column): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                makeMove(row , col - 1, 'X'); // Player is represented by 'X'
                currentPlayer = 'X';
            } else {
                System.out.println("Computer is making a move...");
                int[] move = findBestMove();
                int a,b = 0;
                a = move[0];
                b = move[1];
                System.out.println("Computer is moved to "+ "0"+ a + " "+"0"+b);
                makeMove(move[0], move[1]-1, 'O'); // Computer is represented by 'O'
                currentPlayer = 'O';
            }
            playerTurn = !playerTurn;


            if (checkWin(currentPlayer)) {

                System.out.println("Player " + (currentPlayer == 'X' ? "X" : "O") + " wins!");
                break;
            }


        }
    }


    private void printBoard() {
        // Print column numbers
        System.out.print("   ");
        for (int i = 1; i <= n; i++) {
            if (i <= 9){
                System.out.print("0"+ i + "  ");

            }
            else
            {System.out.print(i + "  ");}
        }
        System.out.println();

        // Print the board rows with separators
        for (int i = 1; i <= n; i++) {
            if (i <= 9){
                System.out.print("0"+ i );
            }
            else
            {System.out.print(i);}


            // Print row cells with vertical separators
            for (int j = 0; j <n; j++) {
                System.out.print("| " + board[i][j] + " ");
            }
            System.out.println("|");

            // Print horizontal row separator
            if (i < n ) {
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

        if (boardFull()){
            System.out.println("It is draw");
            return false;
        }

        // Make the move
        board[row][col] = player;
        return true;
    }


    private int[] findBestMove() {
        Random random = new Random();
        int row, col;
        boolean[][] moveMade = new boolean[n][n]; // Keep track of the moves already made
        do {
            row = random.nextInt(n); // Generate a random row index within the range [0, n-1]
            col = random.nextInt(n);
        } while (!isMoveValid(row, col) || moveMade[row][col]||(row == 0 && col == 0)); // Repeat until a valid and unmade move is found
        moveMade[row][col] = true; // Mark the move as made
        return new int[] {row, col};
    }


    private boolean isMoveValid(int row, int col) {
        // Check if the move is within the board
        if (row < 0 || row >= n || col < 0 || col >= n) {
            return false;
        }

        // Check if the specified cell is empty
        if (board[row][col] != ' ') {
            return false;
        }

        return true;
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the size of the board: ");
        int n = scanner.nextInt();
        System.out.println("Enter the number of pieces connected to win: ");
        int m = scanner.nextInt();
        System.out.println("Who moves first? 1 for player, 2 for computer: ");
        int firstMove = scanner.nextInt();
        ChessGame game = new ChessGame(n, m, firstMove == 1);
        game.playGame();
    }
}
