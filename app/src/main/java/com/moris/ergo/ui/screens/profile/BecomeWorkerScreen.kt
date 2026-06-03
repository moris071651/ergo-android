package com.moris.ergo.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moris.ergo.TitleViewModel
import com.moris.ergo.data.dto.AddressDTO
import com.moris.ergo.data.dto.BecomeWorkerRequestDTO

@Composable
fun BecomeWorkerScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    titleViewModel: TitleViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val skillsOptions by viewModel.skillsOptions.collectAsState()

    LaunchedEffect(Unit) {
        titleViewModel.setTitle("Become Worker")
        viewModel.loadUserAddresses()
        viewModel.loadSkillsOptions()
    }

    val context = LocalContext.current

    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var bio by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var selectedSkill by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxSize()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AddressDropdown(
                label = "Primary Work Base",
                items = addresses,
                selectedId = selectedAddressId,
                onSelect = { selectedAddressId = it.id }
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("About You / Bio") },
                placeholder = { Text("Describe your services...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 7
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Rad (km)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Rounded.Radar, null, modifier = Modifier.size(18.dp)) }
                )

                OutlinedTextField(
                    value = experience,
                    onValueChange = { input ->
                        val cleanInput = input.filter { it.isDigit() }

                        if (cleanInput.isEmpty()) {
                            experience = ""
                        }
                        else {
                            val num = cleanInput.toIntOrNull()
                            if (num != null && num in 1..99) {
                                experience = num.toString()
                            }
                            else {
                                Toast.makeText(
                                    context,
                                    "Experience must be between 1 and 99 years",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    label = { Text("Exp (years)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Rounded.WorkHistory, null, modifier = Modifier.size(18.dp)) }
                )
            }

            SkillsDropdownSelector(
                selectedSkill = selectedSkill,
                skillsOptions = skillsOptions,
                onSkillSelected = { selectedSkill = it }
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            enabled = !isLoading && selectedAddressId != null && bio.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.large,
            onClick = {
                viewModel.becomeWorker(
                    BecomeWorkerRequestDTO(
                        addressId = selectedAddressId!!,
                        bio = bio,
                        serviceRadiusKm = radius.toIntOrNull() ?: 0,
                        experienceYears = experience.toIntOrNull() ?: 0,
                        skills = listOf(selectedSkill)
                    ),
                    onSuccess = onSuccess
                )
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            }
            else {
                Icon(Icons.Rounded.Bolt, null)
                Spacer(Modifier.width(8.dp))
                Text("Activate Worker Account", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDropdown(
    label: String,
    items: List<AddressDTO>,
    selectedId: String?,
    onSelect: (AddressDTO) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = items.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.label ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Choose an address") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            leadingIcon = { Icon(Icons.Rounded.Map, null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            items.forEach { address ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(address.label, fontWeight = FontWeight.Bold)
                            address.city?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
                        }
                    },
                    onClick = {
                        onSelect(address)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsDropdownSelector(
    selectedSkill: String,
    skillsOptions: List<String>,
    onSkillSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedSkill,
            onValueChange = {},
            readOnly = true,
            label = { Text("Skill") },
            placeholder = { Text("Select a skill...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Handyman,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            skillsOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onSkillSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

