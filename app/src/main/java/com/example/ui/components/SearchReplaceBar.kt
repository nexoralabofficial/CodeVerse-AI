package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SearchReplaceBar(
  searchQuery: String,
  replaceQuery: String,
  onSearchChange: (String) -> Unit,
  onReplaceChange: (String) -> Unit,
  onReplaceCurrent: () -> Unit,
  onReplaceAll: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = BgSurfaceElevated,
    shadowElevation = 4.dp
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("Find...", color = TextMuted, fontSize = 12.sp) },
          modifier = Modifier.weight(1f).height(46.dp).testTag("search_input"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = BorderSubtle,
            focusedContainerColor = BgDarkest,
            unfocusedContainerColor = BgDarkest
          ),
          shape = RoundedCornerShape(6.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        OutlinedTextField(
          value = replaceQuery,
          onValueChange = onReplaceChange,
          placeholder = { Text("Replace...", color = TextMuted, fontSize = 12.sp) },
          modifier = Modifier.weight(1f).height(46.dp).testTag("replace_input"),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentPrimary,
            unfocusedBorderColor = BorderSubtle,
            focusedContainerColor = BgDarkest,
            unfocusedContainerColor = BgDarkest
          ),
          shape = RoundedCornerShape(6.dp)
        )

        IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close Search", tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilledTonalButton(
          onClick = onReplaceCurrent,
          modifier = Modifier.height(30.dp).testTag("replace_button"),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
          colors = ButtonDefaults.filledTonalButtonColors(containerColor = BgSurface, contentColor = AccentCyan),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text("Replace", fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        FilledTonalButton(
          onClick = onReplaceAll,
          modifier = Modifier.height(30.dp).testTag("replace_all_button"),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
          colors = ButtonDefaults.filledTonalButtonColors(containerColor = BgSurface, contentColor = AccentPrimary),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text("Replace All", fontSize = 11.sp)
        }
      }
    }
  }
}
