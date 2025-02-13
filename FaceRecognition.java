import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FaceRecognition {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static  CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt2.xml");
    private static  List<Mat> trainingImages = new ArrayList<>();
    private static  List<String> trainingNames = new ArrayList<>();
    private static String currentPersonFolder;

    public static void main(String[] args) {
        if (faceDetector.empty()) {
            System.err.println("Error: LBP Cascade not loaded!");
            return;
        }

        loadTrainingData("images");

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Error: Camera could not be opened!");
            return;
        }

        JFrame frameWindow = new JFrame("Face Recognition");
        JLabel imageLabel = new JLabel();
        JButton trainButton = createTrainButton(camera);

        frameWindow.add(imageLabel, BorderLayout.CENTER);
        frameWindow.add(trainButton, BorderLayout.SOUTH);
        frameWindow.setSize(640, 480);
        frameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameWindow.setVisible(true);

        new Thread(() -> captureFrames(camera, imageLabel, frameWindow)).start();
    }

    private static JButton createTrainButton(VideoCapture camera) {
        JButton trainButton = new JButton("Train with Current Face");
        trainButton.addActionListener(e -> {
            String personName = JOptionPane.showInputDialog(null, "Enter name for new person:", "Name Person", JOptionPane.QUESTION_MESSAGE);
            if (personName != null && !personName.trim().isEmpty()) {
                currentPersonFolder = "images" + File.separator + personName.trim();
                new File(currentPersonFolder).mkdirs();
                for (int i = 0; i < 10; i++) {
                    trainWithCurrentFace(camera);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid name. Training canceled.");
            }
        });
        return trainButton;
    }

    private static void loadTrainingData(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) return;

        for (File personFolder : folder.listFiles(File::isDirectory)) {
            for (File file : personFolder.listFiles(f -> f.isFile() && f.getName().endsWith(".jpg"))) {
                Mat img = Imgcodecs.imread(file.getAbsolutePath());
                if (!img.empty()) {
                    trainingImages.add(img);
                    trainingNames.add(personFolder.getName());
                }
            }
        }
        System.out.println("Training data loaded: " + trainingNames.size() + " images.");
    }

    private static void captureFrames(VideoCapture camera, JLabel imageLabel, JFrame frameWindow) {
        Mat frame = new Mat();
        while (camera.read(frame)) {
            Mat processedFrame = new Mat();
            detectAndDisplay(frame, processedFrame);
            imageLabel.setIcon(new ImageIcon(matToBufferedImage(processedFrame)));
            frameWindow.repaint();
        };
    }

    private static void detectAndDisplay(Mat colorFrame, Mat processedFrame) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(colorFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayFrame, faces);

        colorFrame.copyTo(processedFrame);
        for (Rect rect : faces.toArray()) {
            int marginX = (int) (rect.width * 0.1);
            int marginY = (int) (rect.height * 0.1);
            Rect smallerRect = new Rect(
                    Math.max(rect.x + marginX, 0),
                    Math.max(rect.y + marginY, 0),
                    Math.max(rect.width - 2 * marginX, 1),
                    Math.max(rect.height - 2 * marginY, 1)
            );

            Imgproc.rectangle(processedFrame, smallerRect.tl(), smallerRect.br(), new Scalar(0, 255, 0), 2);
            Mat faceROI = colorFrame.submat(smallerRect);
            String name = compareFace(faceROI);
            Imgproc.putText(processedFrame, name, new Point(smallerRect.x, smallerRect.y - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(255, 0, 0), 2);
        }
    }

    private static void trainWithCurrentFace(VideoCapture camera) {
        Mat frame = new Mat();
        if (camera.read(frame)) {
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(grayFrame, faces);

            for (Rect rect : faces.toArray()) {
                Mat faceROI = frame.submat(rect);
                Mat resizedFace = new Mat();
                Imgproc.resize(faceROI, resizedFace, new Size(100, 100));
                String faceName = currentPersonFolder + File.separator + "face_" + System.currentTimeMillis() + ".jpg";
                Imgcodecs.imwrite(faceName, resizedFace);
                trainingImages.add(resizedFace);
                trainingNames.add(new File(currentPersonFolder).getName());
                System.out.println("Added new training face: " + faceName);
            }
        }
    }

    private static String compareFace(Mat face) {
        Mat resizedFace = new Mat();
        Imgproc.resize(face, resizedFace, new Size(100, 100));
        for (int i = 0; i < trainingImages.size(); i++) {
            if (isSameFace(resizedFace, trainingImages.get(i))) {
                return trainingNames.get(i);
            }
        }
        return "Unknown";
    }

    private static boolean isSameFace(Mat face1, Mat face2) {
        Mat face2Resized = new Mat();
        Imgproc.resize(face2, face2Resized, new Size(50, 50));
        Mat hist1 = new Mat(), hist2 = new Mat();
        Imgproc.calcHist(List.of(face1), new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), new MatOfFloat(0f, 256f));
        Imgproc.calcHist(List.of(face2Resized), new MatOfInt(0), new Mat(), hist2, new MatOfInt(256), new MatOfFloat(0f, 256f));
        Core.normalize(hist1, hist1);
        Core.normalize(hist2, hist2);
        return Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL) > 0.8;
    }

    private static BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols(), rows = matrix.rows();
        byte[] data = new byte[cols * rows * (int) matrix.elemSize()];
        matrix.get(0, 0, data);
        BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, cols, rows, data);
        return image;
    }
}
