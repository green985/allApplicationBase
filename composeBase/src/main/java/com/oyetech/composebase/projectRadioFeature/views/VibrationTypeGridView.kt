import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.helpers.vibrationHelper.VibrationType
import com.oyetech.composebase.projectRadioFeature.views.VibrationSelectionView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VibrationTypeGridView(
    types: List<VibrationType>,
    onItemClick: (VibrationType) -> Unit,
    selectedVibrationType: VibrationType? = null,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Her satırda 3 öğe olacak şekilde
        modifier = Modifier.padding(16.dp), // Grid etrafına 16 dp padding
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(types) { type ->
            OutlinedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                onClick = { onItemClick(type) } // Tıklama durumunda type'ı geri döndürür
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isEnable = selectedVibrationType == type
                    VibrationSelectionView(
                        type = type,
                        isEnable = isEnable,
                        size = 75.dp, // Örneğin her öğenin boyutunu 80 dp olarak belirleyelim
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VibrationTypePreview() {

    VibrationTypeGridView(
        types = VibrationType.entries,
        onItemClick = { }
    )
}
