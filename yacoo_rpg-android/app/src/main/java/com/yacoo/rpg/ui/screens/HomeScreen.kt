package com.yacoo.rpg.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.game.*
import com.yacoo.rpg.ui.components.*
import com.yacoo.rpg.ui.theme.*

class TrapezoidShape : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.12f, 0f)
            lineTo(size.width * 0.88f, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun HomeScreen(
    meta: MetaSave,
    heroStats: HeroStats,
    language: AppLanguage = AppLanguage.ENGLISH,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onStartCombat: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = homeLabels(language)
    val bottomContentClearance = bottomNavContentClearance()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomContentClearance)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top HUD
            TopStatsBar(
                stage = meta.bestChapter,
                coins = meta.coins,
                gems = 0,
                power = heroStats.power,
                energy = 35,
                language = language
            )

            // 2. Stage Info Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .cartoonBorder(1.dp, ColorInk, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1C1635))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    GameIcon(icon = GameIconRole.STAR, fontSize = 18f)
                    Text(
                        text = if (language == AppLanguage.KOREAN) "최고 기록 0/12" else "BEST RECORD 0/12",
                        color = ColorTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = labels.chapterSubtitle(meta.bestChapter),
                    color = ColorTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF8E44FF))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    GameIcon(icon = GameIconRole.POWER, fontSize = 24f)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatCompact(heroStats.power),
                        color = ColorTextOnDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 3. Central Hero on Pedestal & Edge vertical buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Shadow/Glow underneath pedestal
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-24).dp)
                        .size(width = 190.dp, height = 40.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xAA8E44FF), Color.Transparent)
                            )
                        )
                )

                // Pedestal (3D Layered Stone Platform)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-30).dp)
                        .size(width = 180.dp, height = 48.dp)
                ) {
                    // 1. Bottom Dark Base Shadow/Slab
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(width = 180.dp, height = 36.dp)
                            .clip(CircleShape)
                            .background(Brush.verticalGradient(listOf(Color(0xFF151025), Color(0xFF090612))))
                            .cartoonBorder(2.dp, ColorInk, CircleShape)
                    )
                    
                    // 2. Middle Stone Slab (Adds 3D thickness)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-4).dp)
                            .size(width = 176.dp, height = 34.dp)
                            .clip(CircleShape)
                            .background(Brush.verticalGradient(listOf(Color(0xFF2E244E), Color(0xFF19132C))))
                            .cartoonBorder(2.dp, ColorInk, CircleShape)
                    )
                    
                    // 3. Top Platform Lip (where character stands)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-10).dp)
                            .size(width = 172.dp, height = 32.dp)
                            .clip(CircleShape)
                            .background(Brush.verticalGradient(listOf(Color(0xFF4A3A74), Color(0xFF281D45))))
                            .cartoonBorder(2.dp, Color(0xFFB75CFF), CircleShape) // Glowing purple lip outline
                    )
                }

                // Hero Paperdoll
                HeroPaperdollCanvas(
                    equipment = meta.equipment,
                    size = 180.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-45).dp)
                )

                // Left side buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SideButton(
                        label = if (language == AppLanguage.KOREAN) "퀘스트" else "Quest",
                        icon = GameIconRole.TREASURE,
                        enabled = true
                    ) {
                        onNavigate(Screen.RUN_MAP)
                    }
                    SideButton(
                        label = if (language == AppLanguage.KOREAN) "출석" else "Daily",
                        icon = GameIconRole.REWARD,
                        enabled = false
                    )
                }

                // Right side buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SideButton(
                        label = if (language == AppLanguage.KOREAN) "이벤트" else "Event",
                        icon = GameIconRole.STAR,
                        enabled = false
                    )
                    SideButton(
                        label = if (language == AppLanguage.KOREAN) "초대" else "Invite",
                        icon = GameIconRole.PLAYER_AVATAR,
                        enabled = false
                    )
                }
            }

            // 4. Bottom Controls Section (Quick Actions + Start CTA + Notice)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Quick Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LobbyQuickActionButton(
                        label = if (language == AppLanguage.KOREAN) "챔터 돌파" else "Chapter",
                        icon = GameIconRole.TREASURE
                    ) {
                        onNavigate(Screen.RUN_MAP)
                    }
                    LobbyQuickActionButton(
                        label = if (language == AppLanguage.KOREAN) "원정" else "Expedition",
                        icon = GameIconRole.CHEST
                    ) {
                        onNavigate(Screen.GACHA)
                    }
                }

                // Start CTA with press animation
                val trapezoid = remember { TrapezoidShape() }
                
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(64.dp)
                        .clip(trapezoid)
                        .background(Color(0xFF1A1025))
                        .cartoonBorder(3.dp, Color(0xFF7A58C8), trapezoid)
                        .clickable {
                            onStartCombat()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                           text = labels.startCombat,
                           color = ColorTextPrimary,
                           fontSize = 18.sp,
                           fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Center
                        ) {
                           Text("⚡", color = Color(0xFFFFCC4D), fontSize = 11.sp)
                           Spacer(modifier = Modifier.width(3.dp))
                           Text("5", color = Color(0xFFFFCC4D), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                // Notice Scroll Banner
                NoticeBanner(language = language)
            }
        }
    }
}

@Composable
private fun SideButton(
    label: String,
    icon: GameIconRole,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .width(56.dp)
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.5f
            }
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .cartoonShadow(2.dp, ColorInk, shape)
                .cartoonBorder(1.5.dp, ColorOutlineSubtle, shape)
                .clip(shape)
                .background(Color(0xFF1C1635)),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(icon = icon, fontSize = 36f)
        }
        Text(
            text = label,
            color = ColorTextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LobbyQuickActionButton(
    label: String,
    icon: GameIconRole,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(110.dp)
            .height(40.dp)
            .cartoonShadow(2.dp, ColorInk, shape)
            .cartoonBorder(1.5.dp, ColorOutlineSubtle, shape)
            .clip(shape)
            .background(Color(0xFF211A3A))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
    ) {
        GameIcon(icon = icon, fontSize = 28f)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = ColorTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun NoticeBanner(language: AppLanguage) {
    val text = if (language == AppLanguage.KOREAN) {
        "픽셀 판타지 어드벤처에 오신 것을 환영합니다! 신규 보급 상자 판매 중..."
    } else {
        "Welcome to Pixel Fantasy Adventure! New supply packs are now available in the shop..."
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0x800B0B18))
            .padding(vertical = 4.dp),
    ) {
        GameIcon(icon = GameIconRole.STAR, fontSize = 20f)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = ColorTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatCompact(value: Int): String = when {
    value >= 1_000_000 -> "${value / 100_000 / 10f}M"
    value >= 1_000 -> "${value / 100 / 10f}K"
    else -> value.toString()
}

private data class HomeLabels(
    val startCombat: String,
    val runs: String,
    val reset: String,
    val chapterTitle: String,
    val rankingTitle: String,
    val quest: String,
    val shop: String,
    val gear: String,
    val upgrade: String,
    val language: String,
    val event: String,
    val explore: String,
    val challenge: String,
    val stage: String,
    val close: String,
    val chapterSubtitle: (Int) -> String
)

private fun homeLabels(language: AppLanguage): HomeLabels = when (language) {
    AppLanguage.KOREAN -> HomeLabels(
        startCombat = "시작",
        runs = "플레이 횟수",
        reset = "초기화",
        chapterTitle = "킹덤 방어전",
        rankingTitle = "도전 기록 랭킹",
        quest = "대탐험",
        shop = "상점",
        gear = "장비",
        upgrade = "특성",
        language = "언어",
        event = "이벤트",
        explore = "탐방",
        challenge = "별 등급 도전",
        stage = "스테이지",
        close = "닫기",
        chapterSubtitle = { chapter -> "${chapter}. 앰버베이 II  ·  최고 생존 ${chapter + 7}일" }
    )
    AppLanguage.ENGLISH -> HomeLabels(
        startCombat = "START",
        runs = "Runs",
        reset = "Reset",
        chapterTitle = "Kingdom Defense",
        rankingTitle = "Challenge Ranking",
        quest = "Quest",
        shop = "Shop",
        gear = "Gear",
        upgrade = "Talent",
        language = "Lang",
        event = "Event",
        explore = "Explore",
        challenge = "Star Challenge",
        stage = "STAGE",
        close = "Close",
        chapterSubtitle = { chapter -> "$chapter. Amber Bay II  ·  Best ${chapter + 7} days" }
    )
}
