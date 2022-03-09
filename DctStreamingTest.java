import java.util.HashMap;

public class DctStreamingTest {
    public static void testTMatrixValues() {
        double[][] tMatrix = EncodeDecodeUtils.getTMatrix();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                System.out.print(tMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void testTMatrixTranspose() {
        double[][] transposeMatrix = EncodeDecodeUtils.transposeMatrix(EncodeDecodeUtils.getTMatrix());
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                System.out.print(transposeMatrix[i][j] + "     ");
            }
            System.out.println();
        }
    }
    public static void testZigZagOrdering() {
        HashMap<Integer, java.util.List<Integer>> map = Decoder.getSpectralOrderingFor8x8();
        for (int i = 0; i < 64; ++i) {
            System.out.println(map.get(i));
        }
    }
    public static void main(String[] args) {
        // testTMatrixValues();
        // testTMatrixTranspose();
        testZigZagOrdering();
    }
}
