package com.example.hillcipher;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class HillCipher implements Initializable {

    @FXML
    private TextArea plainTxt, cipherTxt;
    @FXML
    private Button decryptBtn;
    @FXML
    private Button encryptBtn;
    @FXML
    private Button generateBtn;

    @FXML
    private ComboBox inpuTxt_ComboBox;

    @FXML
    private TextField keyTxt;

    int getMatrixSize() {
        String getSize = (String) inpuTxt_ComboBox.getValue();
        int stringSize = Integer.parseInt(getSize);
        return stringSize;

    }

    double[][] getKeyMatrix(String key, double[][] keyMatrix) {
        int size = getMatrixSize();
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                keyMatrix[i][j] = (key.charAt(k)) % 65;
                k++;
            }
        }
        return keyMatrix;
    }

    Boolean checkKey(String key) {
        if (key.length() == 0) {
            return false;
        }
        try {
            double[][] keyMatrix = new double[getMatrixSize()][getMatrixSize()];
            double iMD = findInverseMod26(determinant(getKeyMatrix(key, keyMatrix)));
            return true;
        } catch (IllegalArgumentException e) {
            key = "";
            return false;
        }
    }

    String randomKey() {
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String key = "";
        int size = getMatrixSize() * getMatrixSize();
        while (key.length() < size) {
            char extra = upper.charAt((int) ((Math.random() * 25) + 0));
            key += extra;
        }
        return key;
    }

    @FXML
    void generateKey() {
        String key = "";
        if (getMatrixSize() <= 7) {
            while (!checkKey(key)) {
                key = randomKey();
            }
        } else {
            key = randomKey();
        }
        keyTxt.setText(key);
    }

    double findInverseMod26(double b) {
        for (int i = 1; i < 26; i++) {
            if ((b * i) % 26 == 1) {
                return i;
            }
        }
        throw new IllegalArgumentException("The inverse modulo does not exist.");
    }

    double[][] adjoint(double[][] matrix) {
        int size = getMatrixSize();
        double[][] adjointMatrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int sign = ((i + j) % 2 == 0) ? 1 : -1;
                double[][] minor = minorMatrix(matrix, i, j);
                adjointMatrix[j][i] = Math.floorMod((int) (sign * determinant(minor)), 26);
            }
        }
        System.out.println("adj: ");
        printMatrix(adjointMatrix);
        return adjointMatrix;
    }

    double[][] minorMatrix(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] minor = new double[n - 1][n - 1];
        int p = 0, q = 0;
        for (int i = 0; i < n; i++) {
            if (i != row) {
                for (int j = 0; j < n; j++) {
                    if (j != col) {
                        minor[p][q++] = matrix[i][j];
                    }
                }
                p++;
                q = 0;
            }
        }
        return minor;
    }

    double[][] inverseMatrix(double[][] matrix) {
        int size = getMatrixSize();
        double[][] inverse = new double[size][size];
        double det = determinant(matrix);
        double inverseDet = findInverseMod26(det);
        double[][] adj = adjoint(matrix);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                inverse[i][j] = Math.floorMod((int) (adj[i][j] * inverseDet), 26);
            }
        }
        System.out.println("Inverse: ");
        printMatrix(inverse);
        return inverse;
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    void hillVector(double[][] cipherMatrix,
                    double[][] keyMatrix,
                    double[][] messageVector) {
        int size = getMatrixSize();
        int x, i, j;
        for (i = 0; i < size; i++) {
            for (j = 0; j < 1; j++) {
                cipherMatrix[i][j] = 0;
                for (x = 0; x < size; x++) {
                    cipherMatrix[i][j] +=
                            keyMatrix[i][x] * messageVector[x][j];
                }
                cipherMatrix[i][j] = Math.floorMod((int) cipherMatrix[i][j], 26);
            }
        }
    }
    public double determinant(double[][] a) {
        double det = 0, sign = 1, p = 0, q = 0;
        int n = a.length;
        if (n == 1) {
            det = a[0][0];
        } else {
            double[][] b = new double[n - 1][n - 1];
            for (int x = 0; x < n; x++) {
                p = 0;
                q = 0;
                for (int i = 1; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (j != x) {
                            b[(int) p][(int) q++] = a[i][j];
                            if (q % (n - 1) == 0) {
                                p++;
                                q = 0;
                            }
                        }
                    }
                }
                det = det + a[0][x] * determinant(b) * sign;
                sign = -sign;
            }
        }
        return det % 26;
    }

    @FXML
    private void Encrypt(ActionEvent event) {
        String msg = plainTxt.getText();
        int size = getMatrixSize();
        String key = keyTxt.getText();
        if (key.length() != size * size) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("This is an alert!");
            alert.setContentText("Qelesi duhet te jete " + size * size + " karaktere!!");
            alert.showAndWait();
        } else {
            double[][] keyMatrix = new double[size][size];
            getKeyMatrix(key, keyMatrix);
            hillCipher(msg, keyMatrix, true);
        }
    }

    @FXML
    private void Decrypt(ActionEvent event) {
        String msg = plainTxt.getText();
        int size = getMatrixSize();
        String key = keyTxt.getText();
        if (key.length() != size * size) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("This is an alert!");
            alert.setContentText("Qelesi duhet te jete " + size * size + " karaktere!!");
            alert.showAndWait();
        } else {
            double[][] keyMatrix = new double[size][size];
            getKeyMatrix(key, keyMatrix);
            hillCipher(msg, keyMatrix, false);
        }
    }
    void hillCipher(String message, double[][] keyMatrix, boolean mode) {
        int size = getMatrixSize();

        if (message.length() % size != 0) {
            int padSize = size - (message.length() % size);
            for (int i = 0; i < padSize; i++) {
                message += 'X';
            }
        }

        String finalCipher = "";

        for (int i = 0; i < message.length(); i += size) {
            String blockMsg = "";
            String temp = message;
            double[][] messageVector = new double[size][1];
            blockMsg = temp.substring(i, i + size);

            for (int n = 0; n < size; n++) {
                messageVector[n][0] = (blockMsg.charAt(n)) % 65;
            }

            double[][] cipherMatrix = new double[size][1];

            if (!mode) {
                if (size == 1) {
                    double[][] v = new double[size][size];
                    v[0][0] = findInverseMod26(keyMatrix[0][0]);
                    hillVector(cipherMatrix, v, messageVector);
                } else {
                    hillVector(cipherMatrix, inverseMatrix(keyMatrix), messageVector);
                }
                String plainText = "";
                for (int n = 0; n < size; n++) {
                    plainText += (char) (cipherMatrix[n][0] + 65);
                }
                finalCipher += plainText;
            } else {
                hillVector(cipherMatrix, keyMatrix, messageVector);
                String cipherText = "";
                for (int n = 0; n < size; n++) {
                    cipherText += (char) (cipherMatrix[n][0] + 65);
                }
                finalCipher += cipherText;
            }
        }

        System.out.println("Ciphertext: " + finalCipher);
        cipherTxt.setText(finalCipher);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> inputSizes = FXCollections.observableArrayList("1", "2", "3", "4");
        inpuTxt_ComboBox.setItems(inputSizes);
        inpuTxt_ComboBox.setValue("1");
    }
}
