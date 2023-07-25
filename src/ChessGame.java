package src;

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

    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printBoard();
            if (playerTurn) {
                System.out.println("Enter your move (row column): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                makeMove(row - 1, col - 1, 'X'); // Player is represented by 'X'
            } else {
                System.out.println("Computer is making a move...");
                int[] move = findBestMove();
                makeMove(move[0], move[1], 'O'); // Computer is represented by 'O'
            }
            playerTurn = !playerTurn;
            if (checkWin()) {
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


    private void makeMove(int row, int col, char player) {
        // Implement this method to make a move on the board
        board[row][col] = player;
    }

    private int[] findBestMove() {
        // Implement this method to use alpha-beta pruning to find the best move
        return new int[2]; // replace this
    }

    private boolean checkWin() {
        // Implement this method to check if the game is won
        return false; // replace this
    }

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
