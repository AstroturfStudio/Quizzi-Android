
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun BugReportDialog(
    gameId: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report a Bug") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Choose how you'd like to report the bug:")

                OutlinedButton(
                    onClick = {
                        val emailIntent =
                            Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:admin@astroturf.studio")
                                putExtra(Intent.EXTRA_SUBJECT, "Quizzi Bug Report - Game $gameId")
                            }
                        context.startActivity(Intent.createChooser(emailIntent, "Send Bug Report"))
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Email, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Send Email")
                }

                OutlinedButton(
                    onClick = {
                        val whatsappIntent =
                            Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse(
                                        "https://wa.me/+905447118705?text=" +
                                            Uri.encode("Bug Report for Game: $gameId\n\n"),
                                    )
                            }
                        context.startActivity(whatsappIntent)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Face, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Send WhatsApp Message")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
