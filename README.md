👤 **Face Recognition using OpenCV in Java** 🤖
This project is a Java-based face recognition application that leverages OpenCV for real-time face detection, training, and recognition. It allows users to capture faces via a webcam, train the system with new faces, and recognize faces in real-time. The application is built using OpenCV's Haar Cascade classifier for face detection and histogram comparison for face recognition.

✨ **Features**
🎥 Real-Time Face Detection: Detects faces in real-time using a webcam feed.

📸 Face Training: Allows users to train the system with new faces by capturing multiple images and associating them with a name.

👀 Face Recognition: Recognizes faces by comparing them against a database of trained images.

🖥️ Simple UI: Provides a basic graphical user interface (GUI) for easy interaction.

🌍 Cross-Platform: Works on any platform that supports Java and OpenCV.

🛠️ **Technologies Used**
OpenCV: For face detection, image processing, and histogram comparison.

Java Swing: For creating the graphical user interface (GUI).

Java: The core programming language used for the application.

🚀 **How It Works**
🔍 Face Detection: The application uses OpenCV's Haar Cascade classifier (haarcascade_frontalface_alt2.xml) to detect faces in the webcam feed.

📚 Face Training: Users can train the system by capturing multiple images of a person's face and associating them with a name. These images are saved in a folder and added to the training dataset.

🤖 Face Recognition: The system compares detected faces with the training dataset using histogram comparison. If a match is found, the person's name is displayed; otherwise, the face is labeled as "Unknown."

🛠️ **Setup and Installation**
Prerequisites
☕ Java Development Kit (JDK): Ensure you have JDK 8 or later installed.

📦 OpenCV: Download and install OpenCV for your platform. Add the OpenCV .jar file and native library to your project.

📷 Webcam: A working webcam is required for real-time face detection.

Steps to Run the Project
📥 Clone the Repository:
Copy
git clone https://github.com/your-username/face-recognition-opencv-java.git
cd face-recognition-opencv-java
📂 Download OpenCV:

Download OpenCV from the official website.

Extract the files and locate the opencv-<version>.jar and native library files (e.g., opencv_java<version>.dll for Windows or libopencv_java<version>.so for Linux).

➕ Add OpenCV to Your Project:

Add the opencv-<version>.jar to your project's build path.

Ensure the native library is accessible by setting the java.library.path system property or placing it in a directory included in your system's PATH.

▶️ Run the Application:

Compile and run the FaceRecognition.java file.

Ensure the haarcascade_frontalface_alt2.xml file is in the project directory or provide the correct path in the code.

🎮 **Usage**
🚀 Launch the Application:

The application will open a window displaying the webcam feed with detected faces highlighted.

📸 Train a New Face:

Click the "Train with Current Face" button.

Enter the name of the person when prompted.

The system will capture 10 images of the detected face and save them in a folder named after the person.

👤 Recognize Faces:

The application will automatically recognize faces in the webcam feed and display the associated name if a match is found.

