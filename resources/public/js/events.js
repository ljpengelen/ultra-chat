const eventSource = new EventSource("/message-stream");
const messageList = document.getElementById("message-list");
eventSource.addEventListener("message", event => {
    const template = document.createElement('template');
    template.innerHTML = JSON.parse(event.data);
    messageList.prepend(template.content);
});
