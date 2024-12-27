import zmq
import threading
import subprocess
import json

HOST = '127.0.0.1'
PORT = 50007

ZMQ_HOST = '127.0.0.1'
ZMQ_PORT = 50008

context = zmq.Context()
socket = context.socket(zmq.REQ)
socket.connect(f"tcp://{ZMQ_HOST}:{ZMQ_PORT}")


def handle_client(conn, addr):
    print(f"[CONNECTION] Connected by {addr}")
    with conn:
        while True:
            data = conn.recv(4096)
            if not data:
                break
            # Convert bytes to string
            message = data.decode('utf-8')

            # Possibly parse JSON
            request = json.loads(message)
            print(f"[REQUEST] {request}")

            # TODO: run your ML inference/training logic
            # e.g., model.predict(...) or model.update(...)g

            # Some dummy response
            response = {"status": "OK", "message": "Inference done!"}
            conn.sendall(json.dumps(response).encode('utf-8'))


def start_server():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen()
        print(f"[LISTENING] Server listening on {HOST}:{PORT}")
        while True:
            conn, addr = s.accept()
            thread = threading.Thread(target=handle_client, args=(conn, addr), daemon=True)
            thread.start()


if __name__ == "__main__":
    start_server()
