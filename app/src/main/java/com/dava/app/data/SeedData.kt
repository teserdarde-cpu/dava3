package com.dava.app.data

import com.dava.app.model.Contact
import com.dava.app.model.FollowUp
import com.dava.app.model.NoteItem
import com.dava.app.model.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SeedData {
    fun insert(db: DavaDatabase) {
        db.addContact(Contact(name="Ahmet Yılmaz", role="İlçe Başkanı", district="Çorlu", category="Kişi", lastContact="25.08.2026", nextContact="10.09.2026", note="Gençlik çalışmaları konuşuldu."))
        db.addContact(Contact(name="Mehmet Kaya", role="Muhtar", district="Malkara", category="Kamu", lastContact="18.08.2026", note="Mahalle ulaşım talebi."))
        db.addContact(Contact(name="Zeynep Demir", role="STK Temsilcisi", district="Süleymanpaşa", category="STK", lastContact="10.08.2026"))
        db.addFollowUp(FollowUp(title="Sanayi bölgesi altyapı talebi", context="Çorlu / Kurum", dueDate="12.09.2026", status="Devam", priority=3, progress=50))
        db.addFollowUp(FollowUp(title="Gençlik merkezi önerisi", context="Süleymanpaşa / Proje", dueDate="15.09.2026", status="Açık", priority=3, progress=25))
        db.addFollowUp(FollowUp(title="Ulaşım dosyasını ilet", context="İl Başkanlığı", dueDate="18.09.2026", status="Açık", priority=2, progress=0))
        db.addProject(Project(title="Tekirdağ Buluşmaları", description="Muhtar, esnaf, STK ve kurum ziyaretleri", progress=60, status="Devam"))
        db.addProject(Project(title="Türk Dünyası", description="Kültürel temas ve iş birliği çalışmaları", progress=30, status="Devam"))
        db.addProject(Project(title="Arama-Kurtarma STK'sı", description="Kuruluş süreci", progress=15, status="Planlama"))
        val date = SimpleDateFormat("dd.MM.yyyy", Locale("tr","TR")).format(Date())
        db.addNote(NoteItem(title="Yönetim kurulu toplantısı", body="İlçelerdeki son durum değerlendirildi.", category="Toplantı", createdAt=date))
    }
}
