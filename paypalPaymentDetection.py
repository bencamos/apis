# Using GMAIL imap we can detect paypal transaction via the email they send.
# This script will pull all emails filter out paypal and get all the new "Friends and Family" payments and send the details to an API

import imaplib
import email
from email.header import decode_header
import webbrowser
import os
import subprocess
import sys
import time


def run(cmd):
    subprocess.call(cmd, shell=True)

username = "email"
password = "pass"
imap = imaplib.IMAP4_SSL("imap.gmail.com")
imap.login(username, password)

status, messages = imap.select("INBOX")
N = 10
messages = int(messages[0])

for i in range(messages, messages - N, -1):
    res, msg = imap.fetch(str(i), "(RFC822)")
    for response in msg:
        if isinstance(response, tuple):
            msg = email.message_from_bytes(response[1])
            subject = decode_header(msg["Subject"])[0][0]
            if isinstance(subject, bytes):
                subject = subject.decode()
            if subject.find("got money") == -1:
                a = "1"
            else:
                from_ = msg.get("From")
                if msg.is_multipart():
                    for part in msg.walk():
                        content_type = part.get_content_type()
                        content_disposition = str(
                            part.get("Content-Disposition"))
                        try:
                            body = part.get_payload(decode=True).decode()
                        except:
                            pass
                        if content_type == "text/plain" and "attachment" not in content_disposition:
                            if body.find("Fee") == -1:
                                text_file = open("body.txt", "w")
                                text_file.write(body)
                                text_file.close()
                                run("cat body.txt | grep 'Date:' > temp777.txt")
                                searchString = open('temp777.txt')
                                datafile = open('proccessed.txt')
                                with open('temp777.txt') as f, open('proccessed.txt') as g:
                                    bLines = set([thing.strip()
                                                  for thing in g.readlines()])
                                    for line in f:
                                        line = line.strip()
                                        if line in bLines:
                                            #print("Old Transaction :/")
                                            break
                                        else:
                                            run("cat body.txt | grep 'Date:' >> proccessed.txt")
                                            run("cat body.txt | grep 'Amount received\n.00 EUR' > temp7.txt")
                                            run("sed -i 's/Amount received//g' temp7.txt")
                                            run("sed -i ':a;N;$!ba;s/\n/ /g' temp7.txt")
                                            run("sed -i 's/ //g' temp7.txt")
                                            run("cat body.txt | grep 'image: quote' > temp8.txt")
                                            run("sed -i 's/^...............//' temp8.txt")
                                            run("sed -i 's/................\$//' temp8.txt")
                                            run("sed -i 's/[^0-9]*//g' temp8.txt")
                                            run("sed -i '/you/d' temp7.txt")
                                            data_buffer = ""
                                            bal1 = open("temp7.txt", "r")
                                            data_buffer += bal1.read()
                                            userID = ""
                                            userID1 = open("temp7.txt", "r")
                                            userID += userID1.read()
                                            userID1 = ''.join(c for c in userID3 if c.isdigit())
                                            if "$40" in data_buffer:
                                                plan = "1"
                                            elif "$15" in data_buffer:
                                                plan = "2"
                                            elif "$10" in data_buffer:
                                                plan = "3"
                                            else:
                                                break
                                            print("New Transaction! " + data_buffer + " Giving:" + plan + " UserID: " + userID1 + "")
                                            run("curl http://localhost/giveplan.php?apikey=key\&userid=" + userID1 + "\&plan=" + plan + "")

                            else:
                                a = "1"
                else:
                    print("1")
                    content_type = msg.get_content_type()
                    body = msg.get_payload(decode=True).decode()
                    print(body)
                    if content_type == "text/plain":
                        if body.find("Fee") == -1:
                            a = "1"
                        else:
                            a = "1"
imap.close()
imap.logout()
