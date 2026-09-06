package com.dava.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dava.app.data.DavaDatabase
import com.dava.app.data.Prefs
import com.dava.app.data.SeedData
import com.dava.app.model.Contact
import com.dava.app.model.FollowUp
import com.dava.app.model.NoteItem
import com.dava.app.model.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Gold = Color(0xFFD7AE52)
private val Bg = Color(0xFF090A0C)
private val BgAmoled = Color.Black
private val CardBg = Color(0xFF15171B)
private val CardAlt = Color(0xFF1D2025)
private val Line = Color(0xFF30343B)
private val Muted = Color(0xFFA1A7B0)
private val Good = Color(0xFF54C79A)
private val Warn = Color(0xFFE6A95A)
private val Bad = Color(0xFFE66B65)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DavaDatabase(this)
        val prefs = Prefs(this)
        if (!prefs.seeded()) {
            SeedData.insert(db)
            prefs.setSeeded()
        }
        setContent { DavaApp(db, prefs) }
    }
}

@Composable
private fun DavaApp(db: DavaDatabase, prefs: Prefs) {
    var unlocked by remember { mutableStateOf(false) }
    var themeVersion by remember { mutableIntStateOf(0) }
    val amoled = prefs.themeMode() == "amoled"
    key(themeVersion) {
        DavaTheme(amoled) {
            if (!unlocked) PinScreen(prefs) { unlocked = true }
            else MainShell(db, prefs) { themeVersion++ }
        }
    }
}

@Composable
private fun DavaTheme(amoled: Boolean, content: @Composable () -> Unit) {
    val background = if (amoled) BgAmoled else Bg
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Gold,
            onPrimary = Color(0xFF1A1408),
            background = background,
            surface = CardBg,
            surfaceVariant = CardAlt,
            onSurface = Color(0xFFF4F4F5),
            onSurfaceVariant = Muted,
            outline = Line,
            error = Bad
        ),
        content = content
    )
}

@Composable
private fun ThreeCrescents(modifier: Modifier = Modifier) {
    val emblemRed = Color(0xFFED1C24)

    Canvas(modifier) {

        // Kırmızı yuvarlak zemin
        val outerRadius = size.minDimension * 0.48f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(
            color = emblemRed,
            radius = outerRadius,
            center = center
        )

        // Beyaz çift çember
        drawCircle(
            color = Color.White,
            radius = size.minDimension * 0.435f,
            center = center,
            style = Stroke(width = size.minDimension * 0.018f)
        )

        drawCircle(
            color = Color.White,
            radius = size.minDimension * 0.405f,
            center = center,
            style = Stroke(width = size.minDimension * 0.008f)
        )

        // Sağa bakan hilal
        fun crescent(
            cx: Float,
            cy: Float,
            radius: Float
        ) {
            val crescentCenter = Offset(
                size.width * cx,
                size.height * cy
            )

            drawCircle(
                color = Color.White,
                radius = radius,
                center = crescentCenter
            )

            // Kırmızı kesik sağa kaydırılır.
            // Böylece bütün hilaller sağa bakar.
            drawCircle(
                color = emblemRed,
                radius = radius * 0.82f,
                center = crescentCenter + Offset(
                    radius * 0.43f,
                    -radius * 0.03f
                )
            )
        }

        val r = size.minDimension * 0.155f

        // Referanstaki üçlü yerleşim
        crescent(
            cx = 0.40f,
            cy = 0.34f,
            radius = r
        )

        crescent(
            cx = 0.66f,
            cy = 0.50f,
            radius = r
        )

        crescent(
            cx = 0.40f,
            cy = 0.67f,
            radius = r
        )
    }
}

@Composable
private fun PinScreen(prefs: Prefs, onUnlocked: () -> Unit) {
    val setup = !prefs.hasPin()
    var stage by remember { mutableIntStateOf(0) }
    var pin by remember { mutableStateOf("") }
    var first by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    fun submit() {
        if (pin.length < 4) { error = "PIN en az 4 hane olmalı."; return }
        if (!setup) {
            if (prefs.verifyPin(pin)) onUnlocked() else { error = "PIN yanlış."; pin = "" }
            return
        }
        if (stage == 0) { first = pin; pin = ""; stage = 1; error = "" }
        else if (pin == first) { prefs.setPin(pin); onUnlocked() }
        else { error = "PIN'ler eşleşmiyor."; pin = ""; stage = 0; first = "" }
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 28.dp)) {
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            ThreeCrescents(Modifier.size(106.dp))
            Text("DAVA", fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("Vatan için, millet için.", color = Gold, fontSize = 13.sp)
            Spacer(Modifier.height(30.dp))
            Text(
                if (!setup) "Uygulamaya giriş için PIN kodunu girin"
                else if (stage == 0) "4–6 haneli PIN oluşturun" else "PIN kodunu tekrar girin",
                color = Muted, fontSize = 13.sp
            )
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                repeat(6) { i ->
                    Surface(
                        modifier = Modifier.size(if (i < 4) 13.dp else 10.dp),
                        shape = CircleShape,
                        color = if (i < pin.length) Gold else CardAlt,
                        border = if (i < pin.length) null else androidx.compose.foundation.BorderStroke(1.dp, Line)
                    ) {}
                }
            }
            if (error.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(error, color = Bad, fontSize = 12.sp) }
            Spacer(Modifier.height(24.dp))
            NumericKeypad(pin, onChange = { pin = it; error = "" }, onSubmit = ::submit)
            Spacer(Modifier.height(26.dp))
            Text("“Emrolunduğun gibi dosdoğru ol.”", color = Color(0xFFE4D3AA), fontSize = 12.sp)
            Text("Hud, 112", color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun NumericKeypad(value: String, onChange: (String) -> Unit, onSubmit: () -> Unit) {
    val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    if (key.isBlank()) Spacer(Modifier.size(64.dp))
                    else Surface(
                        modifier = Modifier.size(64.dp).clickable {
                            when (key) {
                                "⌫" -> if (value.isNotEmpty()) onChange(value.dropLast(1))
                                else -> if (value.length < 6) {
                                    val next = value + key
                                    onChange(next)
                                }
                            }
                        },
                        shape = CircleShape,
                        color = CardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Line)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (key == "⌫") Icon(Icons.Default.Backspace, null, tint = Gold)
                            else Text(key, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), enabled = value.length >= 4) { Text("DEVAM") }
    }
}

enum class Tab(val title: String) { HOME("Ana Ekran"), CONTACTS("Temaslar"), FOLLOWUPS("Takip"), PROJECTS("Projeler"), DISTRICTS("11 İlçe"), NOTES("Notlar"), SETTINGS("Ayarlar") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainShell(db: DavaDatabase, prefs: Prefs, onThemeChanged: () -> Unit) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var refresh by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(tab.title, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = { IconButton(onClick = { tab = Tab.SETTINGS }) { Icon(Icons.Default.Settings, "Ayarlar", tint = Gold) } }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CardBg) {
                listOf(Tab.HOME, Tab.CONTACTS, Tab.FOLLOWUPS, Tab.PROJECTS, Tab.DISTRICTS).forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = { Icon(tabIcon(item), item.title) },
                        label = { Text(when(item){Tab.HOME->"Ana";Tab.CONTACTS->"Temas";Tab.FOLLOWUPS->"Takip";Tab.PROJECTS->"Proje";else->"İlçe"}, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad).background(MaterialTheme.colorScheme.background)) {
            when (tab) {
                Tab.HOME -> HomeScreen(db, refresh) { tab = it }
                Tab.CONTACTS -> ContactsScreen(db, refresh) { refresh++ }
                Tab.FOLLOWUPS -> FollowUpsScreen(db, refresh) { refresh++ }
                Tab.PROJECTS -> ProjectsScreen(db, refresh) { refresh++ }
                Tab.DISTRICTS -> DistrictsScreen(db, refresh)
                Tab.NOTES -> NotesScreen(db, refresh) { refresh++ }
                Tab.SETTINGS -> SettingsScreen(db, prefs, onDataChanged = { refresh++ }, onThemeChanged = onThemeChanged)
            }
        }
    }
}

@Composable
private fun tabIcon(tab: Tab) = when(tab) {
    Tab.HOME -> Icons.Default.Home
    Tab.CONTACTS -> Icons.Default.Groups
    Tab.FOLLOWUPS -> Icons.Default.AssignmentTurnedIn
    Tab.PROJECTS -> Icons.Default.Work
    Tab.DISTRICTS -> Icons.Default.Map
    Tab.NOTES -> Icons.Default.NoteAlt
    Tab.SETTINGS -> Icons.Default.Settings
}

@Composable
private fun HomeScreen(db: DavaDatabase, refresh: Int, navigate: (Tab) -> Unit) {
    val contacts = remember(refresh) { db.contacts() }
    val followups = remember(refresh) { db.followUps() }
    val projects = remember(refresh) { db.projects() }
    val notes = remember(refresh) { db.notes() }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15130E)), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ThreeCrescents(Modifier.size(60.dp)); Spacer(Modifier.width(14.dp))
                        Column { Text("DAVA", color = Gold, fontSize = 25.sp, fontWeight = FontWeight.Black); Text("Vatan için, millet için.", color = Muted, fontSize = 12.sp) }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("“Türk, öğün, çalış, güven.”", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("— Atatürk", color = Muted, fontSize = 11.sp)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeTile("Temas", "${contacts.size} kayıt", Icons.Default.Groups, Modifier.weight(1f)) { navigate(Tab.CONTACTS) }
                HomeTile("Takip", "${followups.count { it.status != "Tamam" }} açık", Icons.Default.AssignmentTurnedIn, Modifier.weight(1f)) { navigate(Tab.FOLLOWUPS) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeTile("Projeler", "${projects.size} proje", Icons.Default.Work, Modifier.weight(1f)) { navigate(Tab.PROJECTS) }
                HomeTile("11 İlçe", "Saha görünümü", Icons.Default.Map, Modifier.weight(1f)) { navigate(Tab.DISTRICTS) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeTile("Notlar", "${notes.size} not", Icons.Default.NoteAlt, Modifier.weight(1f)) { navigate(Tab.NOTES) }
                HomeTile("Ayarlar", "Yedek / PIN", Icons.Default.Settings, Modifier.weight(1f)) { navigate(Tab.SETTINGS) }
            }
        }
        item { SectionHeader("Öncelikli takipler") }
        items(followups.filter { it.status != "Tamam" }.sortedByDescending { it.priority }.take(4)) { FollowUpCompact(it) }
    }
}

@Composable
private fun HomeTile(title: String, sub: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp)) { Icon(icon, null, tint = Gold); Spacer(Modifier.height(12.dp)); Text(title, fontWeight = FontWeight.Bold); Text(sub, color = Muted, fontSize = 11.sp) }
    }
}

@Composable
private fun SectionHeader(text: String) { Text(text, color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold) }

@Composable
private fun FollowUpCompact(v: FollowUp) {
    Card(colors = CardDefaults.cardColors(containerColor = CardBg)) {
        Column(Modifier.padding(14.dp)) {
            Row { Text(v.title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold); StatusPill(v.status) }
            Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { v.progress / 100f }, modifier = Modifier.fillMaxWidth()); Text("%${v.progress} · ${v.dueDate}", color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ContactsScreen(db: DavaDatabase, refresh: Int, changed: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tümü") }
    var edit by remember { mutableStateOf<Contact?>(null) }
    var adding by remember { mutableStateOf(false) }
    val all = remember(refresh) { db.contacts() }
    val shown = all.filter { (category == "Tümü" || it.category == category) && (query.isBlank() || listOf(it.name,it.role,it.district,it.note).any { s -> s.contains(query,true) }) }
    ScreenWithFab("Yeni temas", { adding = true }) {
        SearchField(query, { query = it }, "Temas ara")
        ChipRow(listOf("Tümü","Kişi","Kurum","STK","Kamu"), category) { category = it }
        shown.forEach { c ->
            Card(Modifier.fillMaxWidth().clickable { edit = c }, colors = CardDefaults.cardColors(containerColor = CardBg)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(42.dp), shape = CircleShape, color = CardAlt) { Box(contentAlignment=Alignment.Center){ Text(c.name.take(1).uppercase(), color=Gold, fontWeight=FontWeight.Bold) } }
                    Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)){ Text(c.name, fontWeight=FontWeight.Bold); Text(listOf(c.role,c.district).filter{it.isNotBlank()}.joinToString(" · "), color=Muted, fontSize=11.sp) }
                    Icon(Icons.Default.Edit,null,tint=Muted)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
    if (adding) ContactDialog(null, onDismiss={adding=false}, onSave={ db.addContact(it); adding=false; changed() })
    edit?.let { c -> ContactDialog(c, onDismiss={edit=null}, onDelete={db.deleteContact(c.id); edit=null; changed()}, onSave={ new -> db.deleteContact(c.id); db.addContact(new.copy(id=0)); edit=null; changed() }) }
}

@Composable
private fun FollowUpsScreen(db: DavaDatabase, refresh: Int, changed: () -> Unit) {
    var filter by remember { mutableStateOf("Tümü") }
    var edit by remember { mutableStateOf<FollowUp?>(null) }
    var adding by remember { mutableStateOf(false) }
    val all = remember(refresh) { db.followUps() }
    val shown = if (filter == "Tümü") all else all.filter { it.status == filter }
    ScreenWithFab("Yeni takip", { adding = true }) {
        ChipRow(listOf("Tümü","Açık","Devam","Tamam","Ertelendi"), filter) { filter = it }
        shown.forEach { v ->
            Card(Modifier.fillMaxWidth().clickable { edit = v }, colors = CardDefaults.cardColors(containerColor = CardBg)) {
                Column(Modifier.padding(14.dp)) {
                    Row { Text(v.title, Modifier.weight(1f), fontWeight = FontWeight.Bold); StatusPill(v.status) }
                    if (v.context.isNotBlank()) Text(v.context, color=Muted, fontSize=11.sp)
                    Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress={v.progress/100f}, modifier=Modifier.fillMaxWidth());
                    Row { Text("%${v.progress}", color=Gold, fontSize=11.sp); Spacer(Modifier.weight(1f)); Text(v.dueDate, color=Muted, fontSize=11.sp) }
                }
            }; Spacer(Modifier.height(8.dp))
        }
    }
    if (adding) FollowUpDialog(null, onDismiss={adding=false}, onSave={db.addFollowUp(it); adding=false; changed()})
    edit?.let { v -> FollowUpDialog(v, onDismiss={edit=null}, onDelete={db.deleteFollowUp(v.id);edit=null;changed()}, onSave={db.updateFollowUp(it);edit=null;changed()}) }
}

@Composable
private fun ProjectsScreen(db: DavaDatabase, refresh: Int, changed: () -> Unit) {
    var filter by remember { mutableStateOf("Tümü") }
    var edit by remember { mutableStateOf<Project?>(null) }
    var adding by remember { mutableStateOf(false) }
    val all = remember(refresh) { db.projects() }
    val shown = if(filter=="Tümü") all else all.filter{it.status==filter}
    ScreenWithFab("Yeni proje", {adding=true}) {
        ChipRow(listOf("Tümü","Planlama","Devam","Tamam"), filter) { filter=it }
        shown.forEach { p ->
            Card(Modifier.fillMaxWidth().clickable{edit=p}, colors=CardDefaults.cardColors(containerColor=CardBg)) {
                Column(Modifier.padding(14.dp)) {
                    Row { Text(p.title, Modifier.weight(1f), fontWeight=FontWeight.Bold); StatusPill(p.status) }
                    Text(p.description, color=Muted, maxLines=2, overflow=TextOverflow.Ellipsis, fontSize=11.sp)
                    Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress={p.progress/100f}, modifier=Modifier.fillMaxWidth()); Text("%${p.progress}", color=Gold, fontSize=11.sp)
                }
            }; Spacer(Modifier.height(8.dp))
        }
    }
    if(adding) ProjectDialog(null,onDismiss={adding=false},onSave={db.addProject(it);adding=false;changed()})
    edit?.let { p -> ProjectDialog(p,onDismiss={edit=null},onDelete={db.deleteProject(p.id);edit=null;changed()},onSave={db.updateProject(it);edit=null;changed()}) }
}

private val districts = listOf("Süleymanpaşa","Çorlu","Çerkezköy","Ergene","Marmaraereğlisi","Muratlı","Saray","Şarköy","Hayrabolu","Malkara","Kapaklı")
private val districtPos = mapOf(
    "Şarköy" to Pair(.12f,.78f), "Malkara" to Pair(.28f,.62f), "Hayrabolu" to Pair(.40f,.36f), "Süleymanpaşa" to Pair(.48f,.69f),
    "Muratlı" to Pair(.57f,.43f), "Çorlu" to Pair(.68f,.56f), "Marmaraereğlisi" to Pair(.72f,.78f), "Ergene" to Pair(.70f,.38f),
    "Çerkezköy" to Pair(.82f,.28f), "Kapaklı" to Pair(.88f,.19f), "Saray" to Pair(.84f,.08f)
)

@Composable
private fun DistrictsScreen(db: DavaDatabase, refresh: Int) {
    var selected by remember { mutableStateOf("Süleymanpaşa") }
    val contacts = remember(refresh) { db.contacts() }
    val followups = remember(refresh) { db.followUps() }
    val projects = remember(refresh) { db.projects() }
    val dContacts = contacts.filter { it.district.equals(selected,true) }
    val dFollow = followups.filter { it.context.contains(selected,true) }
    val last = dContacts.map { it.lastContact }.filter { it.isNotBlank() }.maxOrNull() ?: "—"
    LazyColumn(contentPadding=PaddingValues(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { Text("İlçe haritasından veya listeden seçim yap.", color=Muted, fontSize=12.sp) }
        item { DistrictMap(selected) { selected=it } }
        item { ChipRow(districts, selected) { selected=it } }
        item {
            Card(colors=CardDefaults.cardColors(containerColor=CardBg)) {
                Column(Modifier.padding(16.dp)) {
                    Text(selected, color=Gold, fontSize=20.sp, fontWeight=FontWeight.Bold); Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        Metric("Temas", dContacts.size.toString(), Modifier.weight(1f)); Metric("Takip", dFollow.count{it.status!="Tamam"}.toString(), Modifier.weight(1f)); Metric("Proje", projects.count{it.description.contains(selected,true)}.toString(), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp)); Text("Son görüşme: $last", color=Muted, fontSize=12.sp)
                }
            }
        }
    }
}

@Composable
private fun DistrictMap(selected: String, onSelect: (String)->Unit) {
    Card(colors=CardDefaults.cardColors(containerColor=Color(0xFF101216)), shape=RoundedCornerShape(20.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth().aspectRatio(1.55f).padding(8.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                drawRoundRect(Color(0xFF1A1D21), cornerRadius=androidx.compose.ui.geometry.CornerRadius(28f,28f), style=Stroke(width=2f))
                districtPos.values.zipWithNext().forEach { (a,b) -> drawLine(Line, Offset(size.width*a.first,size.height*a.second), Offset(size.width*b.first,size.height*b.second), 2f) }
            }
            districtPos.forEach { (name,pos) ->
                val active = name==selected
                Surface(
                    modifier=Modifier.offset(x=maxWidth*pos.first-22.dp, y=maxHeight*pos.second-22.dp).size(44.dp).clickable{onSelect(name)},
                    shape=CircleShape,
                    color=if(active) Gold else CardAlt,
                    border=androidx.compose.foundation.BorderStroke(1.dp, if(active) Gold else Line)
                ) { Box(contentAlignment=Alignment.Center){ Text(name.take(2).uppercase(), color=if(active) Color.Black else Color.White, fontSize=9.sp, fontWeight=FontWeight.Bold) } }
            }
        }
    }
}

@Composable
private fun Metric(label:String,value:String,modifier:Modifier){ Surface(modifier, color=CardAlt, shape=RoundedCornerShape(14.dp)){ Column(Modifier.padding(10.dp)){Text(value,color=Gold,fontSize=20.sp,fontWeight=FontWeight.Bold);Text(label,color=Muted,fontSize=10.sp)} } }

@Composable
private fun NotesScreen(db:DavaDatabase, refresh:Int, changed:()->Unit){
    var q by remember{mutableStateOf("")}; var adding by remember{mutableStateOf(false)}
    val all=remember(refresh){db.notes()}; val shown=all.filter{q.isBlank()||it.title.contains(q,true)||it.body.contains(q,true)}
    ScreenWithFab("Yeni not",{adding=true}){
        SearchField(q,{q=it},"Not ara")
        shown.forEach{n->Card(colors=CardDefaults.cardColors(containerColor=CardBg)){Column(Modifier.padding(14.dp)){Row{Column(Modifier.weight(1f)){Text(n.title,fontWeight=FontWeight.Bold);Text("${n.category} · ${n.createdAt}",color=Muted,fontSize=10.sp)};IconButton(onClick={db.deleteNote(n.id);changed()}){Icon(Icons.Default.Delete,null,tint=Bad)}};Text(n.body,color=Muted,fontSize=12.sp)}};Spacer(Modifier.height(8.dp))}
    }
    if(adding) NoteDialog(onDismiss={adding=false}){db.addNote(it);adding=false;changed()}
}

@Composable
private fun SettingsScreen(db:DavaDatabase,prefs:Prefs,onDataChanged:()->Unit,onThemeChanged:()->Unit){
    val ctx = LocalContext.current
    var pinDialog by remember{mutableStateOf(false)}; var clearDialog by remember{mutableStateOf(false)}; var about by remember{mutableStateOf(false)}; var message by remember{mutableStateOf("")}
    var amoled by remember{mutableStateOf(prefs.themeMode()=="amoled")}
    val backupLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri->if(uri!=null)runCatching{ ctx.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use{it.write(db.exportJson())}; message="Yedek oluşturuldu."}.onFailure{message="Yedekleme başarısız: ${it.message}"}}
    val restoreLauncher=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null)runCatching{val text=ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use{it.readText()}?:error("Dosya okunamadı");db.importJson(text);onDataChanged();message="Yedek geri yüklendi."}.onFailure{message="Geri yükleme başarısız: ${it.message}"}}
    val exportLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){uri->if(uri!=null)runCatching{ctx.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use{it.write(db.exportCsv())};message="Veriler dışa aktarıldı."}.onFailure{message="Dışa aktarma başarısız: ${it.message}"}}
    LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{SettingRow(Icons.Default.Lock,"PIN Kodunu Değiştir","Mevcut PIN doğrulamasıyla"){pinDialog=true}}
        item{Card(colors=CardDefaults.cardColors(containerColor=CardBg)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Settings,null,tint=Gold);Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text("AMOLED görünüm",fontWeight=FontWeight.Bold);Text("Tam siyah arka plan",color=Muted,fontSize=11.sp)};Switch(checked=amoled,onCheckedChange={amoled=it;prefs.setThemeMode(if(it)"amoled" else "dark");onThemeChanged()})}}}
        item{SettingRow(Icons.Default.Save,"Yedekle","Tüm verileri JSON dosyasına kaydet"){backupLauncher.launch("DAVA-yedek.json")}}
        item{SettingRow(Icons.Default.FileOpen,"Geri Yükle","DAVA JSON yedeğini içe aktar"){restoreLauncher.launch(arrayOf("application/json","text/plain"))}}
        item{SettingRow(Icons.Default.UploadFile,"Verileri Dışa Aktar","CSV raporu oluştur"){exportLauncher.launch("DAVA-veri.csv")}}
        item{SettingRow(Icons.Default.DeleteForever,"Verileri Temizle","Temas, takip, proje ve notları sil",danger=true){clearDialog=true}}
        item{SettingRow(Icons.Default.Info,"Hakkında","DAVA v3.0.0"){about=true}}
        if(message.isNotBlank()) item{Text(message,color=Gold,fontSize=12.sp)}
    }
    if(pinDialog) ChangePinDialog(prefs,onDismiss={pinDialog=false}){message=it;pinDialog=false}
    if(clearDialog) AlertDialog(onDismissRequest={clearDialog=false},title={Text("Veriler temizlensin mi?")},text={Text("Temaslar, takipler, projeler ve notlar silinir. PIN korunur.")},confirmButton={TextButton(onClick={db.clearAll();onDataChanged();clearDialog=false;message="Veriler temizlendi."}){Text("Sil",color=Bad)}},dismissButton={TextButton(onClick={clearDialog=false}){Text("Vazgeç")}})
    if(about) AlertDialog(onDismissRequest={about=false},title={Text("DAVA")},text={Text("Kişisel siyasi çalışma ve takip paneli\nSürüm 3.0.0\nVeriler cihaz içinde tutulur.")},confirmButton={TextButton(onClick={about=false}){Text("Tamam")}})
}


@Composable
private fun SettingRow(icon:androidx.compose.ui.graphics.vector.ImageVector,title:String,sub:String,danger:Boolean=false,onClick:()->Unit){Card(Modifier.fillMaxWidth().clickable(onClick=onClick),colors=CardDefaults.cardColors(containerColor=CardBg)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,tint=if(danger)Bad else Gold);Spacer(Modifier.width(12.dp));Column{Text(title,fontWeight=FontWeight.Bold,color=if(danger)Bad else Color.White);Text(sub,color=Muted,fontSize=11.sp)}}}}

@Composable
private fun ScreenWithFab(label:String,onAdd:()->Unit,content:@Composable ColumnScope.()->Unit){Box(Modifier.fillMaxSize()){LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(start=16.dp,end=16.dp,top=12.dp,bottom=90.dp)){item{Column(content=content)}};ExtendedFloatingActionButton(onClick=onAdd,modifier=Modifier.align(Alignment.BottomEnd).padding(18.dp),icon={Icon(Icons.Default.Add,null)},text={Text(label)})}}

@Composable
private fun SearchField(value:String,onValue:(String)->Unit,label:String){OutlinedTextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth(),singleLine=true,label={Text(label)},leadingIcon={Icon(Icons.Default.Search,null)});Spacer(Modifier.height(10.dp))}

@Composable
private fun ChipRow(options:List<String>,selected:String,onSelect:(String)->Unit){androidx.compose.foundation.lazy.LazyRow(horizontalArrangement=Arrangement.spacedBy(7.dp),contentPadding=PaddingValues(bottom=10.dp)){items(options){o->FilterChip(selected=selected==o,onClick={onSelect(o)},label={Text(o,fontSize=11.sp)})}}}

@Composable
private fun StatusPill(status:String){val c=when(status){"Tamam"->Good;"Devam"->Gold;"Ertelendi"->Warn;else->Muted};Surface(color=c.copy(alpha=.14f),shape=RoundedCornerShape(20.dp)){Text(status,color=c,fontSize=10.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(horizontal=9.dp,vertical=4.dp))}}

@Composable
private fun ContactDialog(existing:Contact?,onDismiss:()->Unit,onSave:(Contact)->Unit,onDelete:(()->Unit)?=null){
    var name by remember{mutableStateOf(existing?.name?:"")};var role by remember{mutableStateOf(existing?.role?:"")};var district by remember{mutableStateOf(existing?.district?:"")};var category by remember{mutableStateOf(existing?.category?:"Kişi")};var phone by remember{mutableStateOf(existing?.phone?:"")};var last by remember{mutableStateOf(existing?.lastContact?:"")};var next by remember{mutableStateOf(existing?.nextContact?:"")};var note by remember{mutableStateOf(existing?.note?:"")}
    EditDialog(title=if(existing==null)"Yeni Temas" else "Teması Düzenle",onDismiss=onDismiss,onDelete=onDelete,onSave={if(name.isNotBlank())onSave(Contact(existing?.id?:0,name,role,district,category,phone,last,next,note))}){OutlinedTextField(name,{name=it},label={Text("Ad / Kurum")},modifier=Modifier.fillMaxWidth());OutlinedTextField(role,{role=it},label={Text("Görev")},modifier=Modifier.fillMaxWidth());OutlinedTextField(district,{district=it},label={Text("İlçe")},modifier=Modifier.fillMaxWidth());ChipRow(listOf("Kişi","Kurum","STK","Kamu"),category){category=it};OutlinedTextField(phone,{phone=it},label={Text("Telefon")},modifier=Modifier.fillMaxWidth());OutlinedTextField(last,{last=it},label={Text("Son görüşme")},modifier=Modifier.fillMaxWidth());OutlinedTextField(next,{next=it},label={Text("Sonraki temas")},modifier=Modifier.fillMaxWidth());OutlinedTextField(note,{note=it},label={Text("Not")},modifier=Modifier.fillMaxWidth())}
}

@Composable
private fun FollowUpDialog(existing:FollowUp?,onDismiss:()->Unit,onSave:(FollowUp)->Unit,onDelete:(()->Unit)?=null){
    var title by remember{mutableStateOf(existing?.title?:"")};var context by remember{mutableStateOf(existing?.context?:"")};var due by remember{mutableStateOf(existing?.dueDate?:"")};var status by remember{mutableStateOf(existing?.status?:"Açık")};var priority by remember{mutableIntStateOf(existing?.priority?:1)};var progress by remember{mutableIntStateOf(existing?.progress?:0)}
    EditDialog(if(existing==null)"Yeni Takip" else "Takibi Düzenle",onDismiss,onDelete,{val finalStatus=if(progress>=100)"Tamam" else if(status=="Tamam"&&progress<100)"Devam" else status;onSave(FollowUp(existing?.id?:0,title,context,due,finalStatus,priority,progress))}){OutlinedTextField(title,{title=it},label={Text("Başlık")},modifier=Modifier.fillMaxWidth());OutlinedTextField(context,{context=it},label={Text("Bağlam / İlçe")},modifier=Modifier.fillMaxWidth());OutlinedTextField(due,{due=it},label={Text("Termin")},modifier=Modifier.fillMaxWidth());ChipRow(listOf("Açık","Devam","Tamam","Ertelendi"),status){status=it;if(it=="Tamam")progress=100};Text("Tamamlanma: %$progress",color=Gold);Slider(value=progress.toFloat(),onValueChange={progress=(it/25).toInt()*25},valueRange=0f..100f,steps=3);Text("Öncelik: $priority",color=Muted,fontSize=11.sp);ChipRow(listOf("1","2","3"),priority.toString()){priority=it.toInt()}}
}

@Composable
private fun ProjectDialog(existing:Project?,onDismiss:()->Unit,onSave:(Project)->Unit,onDelete:(()->Unit)?=null){
    var title by remember{mutableStateOf(existing?.title?:"")};var desc by remember{mutableStateOf(existing?.description?:"")};var status by remember{mutableStateOf(existing?.status?:"Planlama")};var progress by remember{mutableIntStateOf(existing?.progress?:0)}
    EditDialog(if(existing==null)"Yeni Proje" else "Projeyi Düzenle",onDismiss,onDelete,{val s=if(progress>=100)"Tamam" else if(status=="Tamam")"Devam" else status;onSave(Project(existing?.id?:0,title,desc,progress,s))}){OutlinedTextField(title,{title=it},label={Text("Proje adı")},modifier=Modifier.fillMaxWidth());OutlinedTextField(desc,{desc=it},label={Text("Açıklama")},modifier=Modifier.fillMaxWidth());ChipRow(listOf("Planlama","Devam","Tamam"),status){status=it;if(it=="Tamam")progress=100};Text("İlerleme: %$progress",color=Gold);Slider(value=progress.toFloat(),onValueChange={progress=(it/5).toInt()*5},valueRange=0f..100f)}
}

@Composable
private fun NoteDialog(onDismiss:()->Unit,onSave:(NoteItem)->Unit){var title by remember{mutableStateOf("")};var body by remember{mutableStateOf("")};var category by remember{mutableStateOf("Not")};EditDialog("Yeni Not",onDismiss,null,{val date=SimpleDateFormat("dd.MM.yyyy",Locale("tr","TR")).format(Date());onSave(NoteItem(title=title,body=body,category=category,createdAt=date))}){OutlinedTextField(title,{title=it},label={Text("Başlık")},modifier=Modifier.fillMaxWidth());OutlinedTextField(body,{body=it},label={Text("Not")},modifier=Modifier.fillMaxWidth());ChipRow(listOf("Not","Toplantı","Saha","Fikir"),category){category=it}}}

@Composable
private fun EditDialog(title:String,onDismiss:()->Unit,onDelete:(()->Unit)?,onSave:()->Unit,content:@Composable ColumnScope.()->Unit){AlertDialog(onDismissRequest=onDismiss,title={Text(title)},text={LazyColumn{item{Column(verticalArrangement=Arrangement.spacedBy(8.dp),content=content)}}},confirmButton={Button(onClick=onSave){Text("Kaydet")}},dismissButton={Row{if(onDelete!=null)TextButton(onClick=onDelete){Text("Sil",color=Bad)};TextButton(onClick=onDismiss){Text("Vazgeç")}}})}

@Composable
private fun ChangePinDialog(prefs:Prefs,onDismiss:()->Unit,onResult:(String)->Unit){var current by remember{mutableStateOf("")};var next by remember{mutableStateOf("")};var confirm by remember{mutableStateOf("")};var error by remember{mutableStateOf("")};AlertDialog(onDismissRequest=onDismiss,title={Text("PIN Kodunu Değiştir")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(current,{current=it.filter(Char::isDigit).take(6)},label={Text("Mevcut PIN")});OutlinedTextField(next,{next=it.filter(Char::isDigit).take(6)},label={Text("Yeni PIN")});OutlinedTextField(confirm,{confirm=it.filter(Char::isDigit).take(6)},label={Text("Yeni PIN tekrar")});if(error.isNotBlank())Text(error,color=Bad,fontSize=11.sp)}},confirmButton={Button(onClick={when{!prefs.verifyPin(current)->error="Mevcut PIN yanlış.";next.length<4->error="Yeni PIN en az 4 hane olmalı.";next!=confirm->error="Yeni PIN'ler eşleşmiyor.";else->{prefs.setPin(next);onResult("PIN değiştirildi.")}}}){Text("Değiştir")}},dismissButton={TextButton(onClick=onDismiss){Text("Vazgeç")}})}
