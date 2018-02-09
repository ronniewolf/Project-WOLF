#/#
 #
 # Project Name:     Neural Network based Motorcycle Ecosystem
 # Author List:      Rohan Mahajan, Heramb Khanvilkar
 # Filename:         GUI.py
 # Function:         retranslateUi(self, Dialog), open_file_acc(self), open_file_gps(self), start_processing(self)
 # Global Variables: 
 #
 #/
 

import sys
from PyQt5 import QtCore, QtGui, QtWidgets
import GPS_CSV, Accelerometer_Parse, clustering



##GUI Window Setup
class Ui_Dialog(object):
    button_mpu6050 = False
    button_gps = False
    def setupUi(self, Dialog):
        Dialog.setObjectName("Dialog")
        Dialog.resize(573, 304)
        font = QtGui.QFont()
        font.setFamily("Calibri")
        font.setBold(True)
        font.setWeight(75)
        Dialog.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("../../Downloads/projectwolf.png"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        Dialog.setWindowIcon(icon)
        self.ok_cancel = QtWidgets.QDialogButtonBox(Dialog)
        self.ok_cancel.setGeometry(QtCore.QRect(220, 260, 141, 32))
        self.ok_cancel.setOrientation(QtCore.Qt.Horizontal)
        self.ok_cancel.setStandardButtons(QtWidgets.QDialogButtonBox.Cancel|QtWidgets.QDialogButtonBox.Ok)
        self.ok_cancel.setObjectName("ok_cancel")
        self.acc_button = QtWidgets.QToolButton(Dialog)
        self.acc_button.setGeometry(QtCore.QRect(520, 70, 25, 19))
        self.acc_button.setObjectName("acc_button")
        self.acc_edit_line = QtWidgets.QLineEdit(Dialog)
        self.acc_edit_line.setGeometry(QtCore.QRect(80, 70, 431, 20))
        self.acc_edit_line.setObjectName("acc_edit_line")
        self.acc_path = QtWidgets.QLabel(Dialog)
        self.acc_path.setGeometry(QtCore.QRect(30, 70, 51, 16))
        font = QtGui.QFont()
        font.setPointSize(12)
        self.acc_path.setFont(font)
        self.acc_path.setObjectName("acc_path")
        self.gps_button = QtWidgets.QToolButton(Dialog)
        self.gps_button.setGeometry(QtCore.QRect(520, 180, 25, 19))
        self.gps_button.setObjectName("gps_button")
        self.gps_path = QtWidgets.QLabel(Dialog)
        self.gps_path.setGeometry(QtCore.QRect(30, 180, 51, 16))
        font = QtGui.QFont()
        font.setPointSize(12)
        self.gps_path.setFont(font)
        self.gps_path.setObjectName("gps_path")
        self.gps_edit_line = QtWidgets.QLineEdit(Dialog)
        self.gps_edit_line.setGeometry(QtCore.QRect(80, 180, 431, 20))
        self.gps_edit_line.setObjectName("gps_edit_line")
        self.label = QtWidgets.QLabel(Dialog)
        self.label.setGeometry(QtCore.QRect(30, 40, 151, 16))
        font = QtGui.QFont()
        font.setPointSize(14)
        self.label.setFont(font)
        self.label.setObjectName("label")
        self.label_2 = QtWidgets.QLabel(Dialog)
        self.label_2.setGeometry(QtCore.QRect(30, 150, 71, 16))
        font = QtGui.QFont()
        font.setPointSize(14)
        self.label_2.setFont(font)
        self.label_2.setObjectName("label_2")
        self.acc_line = QtWidgets.QFrame(Dialog)
        self.acc_line.setGeometry(QtCore.QRect(30, 50, 511, 16))
        self.acc_line.setFrameShape(QtWidgets.QFrame.HLine)
        self.acc_line.setFrameShadow(QtWidgets.QFrame.Sunken)
        self.acc_line.setObjectName("acc_line")
        self.gps_line = QtWidgets.QFrame(Dialog)
        self.gps_line.setGeometry(QtCore.QRect(30, 160, 511, 16))
        self.gps_line.setFrameShape(QtWidgets.QFrame.HLine)
        self.gps_line.setFrameShadow(QtWidgets.QFrame.Sunken)
        self.gps_line.setObjectName("gps_line")
        self.sep1_line = QtWidgets.QFrame(Dialog)
        self.sep1_line.setGeometry(QtCore.QRect(0, 120, 571, 16))
        self.sep1_line.setFrameShape(QtWidgets.QFrame.HLine)
        self.sep1_line.setFrameShadow(QtWidgets.QFrame.Sunken)
        self.sep1_line.setObjectName("sep1_line")
        self.sep2_line = QtWidgets.QFrame(Dialog)
        self.sep2_line.setGeometry(QtCore.QRect(0, 230, 571, 16))
        self.sep2_line.setFrameShape(QtWidgets.QFrame.HLine)
        self.sep2_line.setFrameShadow(QtWidgets.QFrame.Sunken)
        self.sep2_line.setObjectName("sep2_line")
        self.label_3 = QtWidgets.QLabel(Dialog)
        self.label_3.setGeometry(QtCore.QRect(210, 0, 151, 31))
        font = QtGui.QFont()
        font.setPointSize(20)
        self.label_3.setFont(font)
        self.label_3.setObjectName("label_3")

        self.retranslateUi(Dialog)
        self.ok_cancel.accepted.connect(self.start_processing)
        self.ok_cancel.rejected.connect(Dialog.reject)
        self.acc_button.clicked.connect(self.open_file_acc)
        self.gps_button.clicked.connect(self.open_file_gps)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

		#Assigning names to objects to access them easily 
    def retranslateUi(self, Dialog):
        _translate = QtCore.QCoreApplication.translate
        Dialog.setWindowTitle(_translate("Dialog", "ProjectWolF"))
        self.acc_button.setText(_translate("Dialog", "..."))
        self.acc_path.setText(_translate("Dialog", "PATH"))
        self.gps_button.setText(_translate("Dialog", "..."))
        self.gps_path.setText(_translate("Dialog", "PATH"))
        self.label.setText(_translate("Dialog", "Accelerometer File"))
        self.label_2.setText(_translate("Dialog", "GPS File"))
        self.label_3.setText(_translate("Dialog", "Project WolF"))

		#Accelerometer file select window function
    def open_file_acc(self):
        self.button_mpu6050 = True
        temp = QtWidgets.QFileDialog.getOpenFileName(None, 'Open File')
        Accelerometer_Parse.filename_acc = temp[0]
        self.acc_edit_line.setText(temp[0])

		#GPS file select window function
    def open_file_gps(self):
        self.button_gps = True
        temp = QtWidgets.QFileDialog.getOpenFileName(None, 'Open File')
        GPS_CSV.filename_gps = temp[0]
        self.gps_edit_line.setText(temp[0])

		#Start processing on the data using all the modules
    def start_processing(self):
        if(self.button_gps and self.button_mpu6050):
            Accelerometer_Parse.parse_ACC()
            clustering.clustering()
            GPS_CSV.parse_GPS()


if __name__ == "__main__":
    app = QtWidgets.QApplication(sys.argv)
    Dialog = QtWidgets.QDialog()
    ui = Ui_Dialog()
    ui.setupUi(Dialog)
    Dialog.show()
    sys.exit(app.exec_())

