import androidx.compose.ui.window.ComposeViewport
import androidx.compose.ui.ExperimentalComposeUiApi
import com.oyetech.kmpfeatures.KmpFeaturesApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        KmpFeaturesApp()
    }
}
