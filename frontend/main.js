const url = "https://centelha-de-ideias.onrender.com"
const chatBox = document.getElementById('chatBox');
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');

function addMessage(text, sender) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');

    if (sender === 'robot') {
        messageElement.classList.add('robot-message');

        try {
            if (typeof marked !== 'undefined' && typeof marked.parse === 'function') {
                const htmlText = marked.parse(text);
                messageElement.innerHTML = htmlText;

            } else {
                messageElement.textContent = text;
            }
            messageInput.placeholder = "Digite sua mensagem..."
        } catch (e) {
            console.error("Erro ao processar Markdown:", e);
            messageElement.textContent = "Erro ao exibir mensagem formatada:\n" + text;
        }

    } else {
        messageElement.classList.add('user-message');
        messageElement.textContent = text;
    }

    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}

async function getResponse(messageText) {
    messageInput.placeholder = "Processando, aguarde..."

    try {
        const response = await fetch(`${url}/centelha`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(messageText)
        });

        if (!response.ok) {
            const error = await response.json();
            console.log(error)
            alert(error.message);
            return
        }

        const data = await response.json();
        addMessage(data.text, "robot");

    } catch (error) {
        addMessage(error.text, 'robot')
    }
    messageInput.disabled = false;
    sendButton.disabled = false;
}

window.onload = function () {
    addMessage("Olá, como posso ajudar a ter ideias?", 'robot');
    addMessage("Lembre, ajudaria muito se deixasse claro o tema, o tipo do projeto e seus interesses", 'robot');
};

sendButton.addEventListener('click', function () {
    const messageText = messageInput.value.trim();
    if (messageText) {
        addMessage(messageText, 'user');
        messageInput.disabled = true;
        sendButton.disabled = true;
        getResponse(messageText)
        messageInput.value = '';
    }
});

messageInput.addEventListener('keypress', function (event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendButton.click();
    }
});