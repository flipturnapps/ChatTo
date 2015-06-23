# ChatTo
Encrypts SMS messages.
###About branches
You may notice this project consists of nearly half a dozen branches, some of which are significantly behind master.  These are practice mini-applications I produced while writing ChatTo.  ChatTo master is currently a fully-functional Android application when paired with [ChatToServer](https://github.com/flipturnapps/ChatToServer). This app has not been published yet, and there is currently no consistent server-hosting for ChatToServer, something which is necessary for ChatTo to be useful.
###How it works
There are multitudes of chat appliations on Google Play that encrypt their users' messages.  How is ChatTo different?  ChatTo does not send its user's messages through the internet via data packages like other messaging apps.  Conversely, all messages are sent entirely through normal SMS.  However, the messages sent are unreadable via the messaging app or phone service providers. This is because the messages are encrypted with a key sent down by ChatToServer. The message contents (encrypted) is sent by SMS, the key by the server, and the decryption is done on the recipient's ChatTo app.
####TL;DR:
The difference: messages are broken into two parts, the message and the key.  They aren't sent together, so users can be certain that their phone providers, third party applications, flipturnapps will be unable to read their messages.
