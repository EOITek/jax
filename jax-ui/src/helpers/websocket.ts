import { Project } from '@/constants';
import type { WebSocketConfig, WebSocketData } from '@/models';

const wsReadyState = {
  CONNECTING: 0,
  OPEN: 1,
  CLOSING: 2,
  CLOSED: 4,
};

export class SocketApi {
  ws: WebSocket = null;
  successCallback = null;
  closeCallback = null;
  errorCallback = null;
  onMessageCallback = null;

  constructor(url: string,
              data: WebSocketData,
              { successCallback, closeCallback, errorCallback, onMessageCallback }: WebSocketConfig) {

    const { protocol, host } = location;
    const wsUrl = `ws${protocol.toLowerCase().slice(4)}//${host}/${Project.serviceName || ''}${url}`;

    this.ws = new WebSocket(wsUrl);
    this.successCallback = successCallback;
    this.errorCallback = errorCallback;
    this.closeCallback = closeCallback;
    this.onMessageCallback = onMessageCallback;
    this.onMessage();
    this.initEvent();
    this.send(data);
  }

  send(data: WebSocketData) {
    switch (this.ws.readyState) {
      case wsReadyState.CONNECTING:
        setTimeout(() => this.send(data) ,1000);
        break;
      case wsReadyState.OPEN:
        data.message = JSON.stringify(data.message);
        this.ws.send(JSON.stringify(data));
        break;
      case wsReadyState.CLOSING:
        setTimeout(() => this.send(data) ,1000);
        break;
      case wsReadyState.CLOSED:
        this.closeCallback();
        break;
      default:
        break;
    }
  }

  onMessage() {
    this.ws.onmessage = ({ data }: { data: string }) => {
      this.onMessageCallback && this.onMessageCallback(JSON.parse(data));
    };
  }

  initEvent() {
    this.ws.addEventListener('close', this.handlerClose);
    this.ws.addEventListener('open', this.handlerSuccess);
    this.ws.addEventListener('error', this.handlerError);
  }

  close() {
    if (!this.ws) return;

    this.ws.removeEventListener('close', this.handlerClose);
    this.ws.removeEventListener('open', this.handlerSuccess);
    this.ws.removeEventListener('error', this.handlerError);
    this.ws.close();
    this.ws = null;
  }

  handlerClose = () => {
    this.closeCallback && this.closeCallback();
  };

  handlerSuccess = () => {
    this.successCallback && this.successCallback();
  };

  handlerError = () => {
    this.errorCallback && this.errorCallback();
  };
}
