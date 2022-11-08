# -*- coding: utf-8 -*-
"""
Created on Tue Aug 23 16:28:44 2022

@author: 리경록
"""

import socket as s
import os
import _thread as t
import os.path

#mainPath = r"C:\Users\Lee\Desktop\새 폴더"
mainPath = r"D:"

def send_list(List, conn):
    for st in List:
        conn.send(st.encode('utf-8'))

def file_list(path):
    temp = []
    
    for (root, directories, files) in os.walk(path):
        for d in directories:
            temp.append(d+"    ")
        
        temp.append("@@file@@    ")
        
        for f in files:
            temp.append(f+"    ")
            
        return temp
    
def sendFile(path, conn):
    print("start sending: %path")
    with open(path, "rb") as file:
        try:
            data = file.read(4096)
            while data:
                conn.sendall(data)
                data = file.read(4096)
            print("End Sending")
        except Exception as ex:
            print("Sending Failed: ")
            
def sendFileSize(path, conn):
    size = os.path.getsize(path)
    print("size: " + str(size))
    conn.sendall(str(size).encode('utf-8'))
            
        


def threaded_client(conn, addr):
    while True:
        
        response = conn.recv(2048).decode().strip()
        
        if response == "initial":
            print(response)
            conn.sendall(bytearray((mainPath).encode()))
        
        elif response == "quit":
            print(response)
            conn.close()
            break
        
        elif response == "list":
            print(response)
            send_list(file_list(mainPath), conn)
            
        elif response[0:4] == "down":
            name = response.split("@@@")
            print(name)
            sendFile(name[1], conn)
            
        elif response[0:6] == "folder":
            print(response)
            location = response.split("@@@")
            send_list(file_list(location[1]), conn)
        
        elif response[0:4] == "size":
            path = response.split("@@@")
            print(path[0])
            sendFileSize(path[1], conn)
            
        


ipAddr = input("ip 주소: ")
mpath = r"" + input("위치: ")
mainPath= mpath


serverSock = s.socket(s.AF_INET, s.SOCK_STREAM)
serverSock.bind((ipAddr, 9000))
serverSock.listen(10)

print("server start")

while True:
    conn, addr = serverSock.accept()
    print("Connected to:", addr)
    t.start_new_thread(threaded_client, (conn, addr))
    
    
    



