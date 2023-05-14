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
