package org.eski.util

import androidx.compose.ui.input.key.Key


fun getKeyName(keyCode: Long): String {
  return when (keyCode) {
    // Directional keys
    Key.DirectionLeft.keyCode -> "Left Arrow"
    Key.DirectionRight.keyCode -> "Right Arrow"
    Key.DirectionUp.keyCode -> "Up Arrow"
    Key.DirectionDown.keyCode -> "Down Arrow"

    // Space, Enter, Tab
    Key.Spacebar.keyCode -> "Spacebar"
    Key.Enter.keyCode -> "Enter"
    Key.Tab.keyCode -> "Tab"
    Key.Backspace.keyCode -> "Backspace"

    // Alphabet keys (A-Z)
    Key.A.keyCode -> "A"
    Key.B.keyCode -> "B"
    Key.C.keyCode -> "C"
    Key.D.keyCode -> "D"
    Key.E.keyCode -> "E"
    Key.F.keyCode -> "F"
    Key.G.keyCode -> "G"
    Key.H.keyCode -> "H"
    Key.I.keyCode -> "I"
    Key.J.keyCode -> "J"
    Key.K.keyCode -> "K"
    Key.L.keyCode -> "L"
    Key.M.keyCode -> "M"
    Key.N.keyCode -> "N"
    Key.O.keyCode -> "O"
    Key.P.keyCode -> "P"
    Key.Q.keyCode -> "Q"
    Key.R.keyCode -> "R"
    Key.S.keyCode -> "S"
    Key.T.keyCode -> "T"
    Key.U.keyCode -> "U"
    Key.V.keyCode -> "V"
    Key.W.keyCode -> "W"
    Key.X.keyCode -> "X"
    Key.Y.keyCode -> "Y"
    Key.Z.keyCode -> "Z"

    // Number keys (0-9)
    Key.Zero.keyCode -> "0"
    Key.One.keyCode -> "1"
    Key.Two.keyCode -> "2"
    Key.Three.keyCode -> "3"
    Key.Four.keyCode -> "4"
    Key.Five.keyCode -> "5"
    Key.Six.keyCode -> "6"
    Key.Seven.keyCode -> "7"
    Key.Eight.keyCode -> "8"
    Key.Nine.keyCode -> "9"

    // Function keys
    Key.F1.keyCode -> "F1"
    Key.F2.keyCode -> "F2"
    Key.F3.keyCode -> "F3"
    Key.F4.keyCode -> "F4"
    Key.F5.keyCode -> "F5"
    Key.F6.keyCode -> "F6"
    Key.F7.keyCode -> "F7"
    Key.F8.keyCode -> "F8"
    Key.F9.keyCode -> "F9"
    Key.F10.keyCode -> "F10"
    Key.F11.keyCode -> "F11"
    Key.F12.keyCode -> "F12"

    // Numpad keys
    Key.NumPad0.keyCode -> "Numpad 0"
    Key.NumPad1.keyCode -> "Numpad 1"
    Key.NumPad2.keyCode -> "Numpad 2"
    Key.NumPad3.keyCode -> "Numpad 3"
    Key.NumPad4.keyCode -> "Numpad 4"
    Key.NumPad5.keyCode -> "Numpad 5"
    Key.NumPad6.keyCode -> "Numpad 6"
    Key.NumPad7.keyCode -> "Numpad 7"
    Key.NumPad8.keyCode -> "Numpad 8"
    Key.NumPad9.keyCode -> "Numpad 9"
    Key.NumPadAdd.keyCode -> "Numpad +"
    Key.NumPadSubtract.keyCode -> "Numpad -"
    Key.NumPadMultiply.keyCode -> "Numpad *"
    Key.NumPadDivide.keyCode -> "Numpad /"
    Key.NumPadDot.keyCode -> "Numpad ."
    Key.NumPadEnter.keyCode -> "Numpad Enter"

    // Symbol keys on main keyboard
    Key.Minus.keyCode -> "Minus (-)"
    Key.Equals.keyCode -> "Equals (=)"
    Key.LeftBracket.keyCode -> "Left Bracket ([)"
    Key.RightBracket.keyCode -> "Right Bracket (])"
    Key.Backslash.keyCode -> "Backslash (\\)"
    Key.Semicolon.keyCode -> "Semicolon (;)"
    Key.Apostrophe.keyCode -> "Apostrophe (')"
    Key.Comma.keyCode -> "Comma (,)"
    Key.Period.keyCode -> "Period (.)"
    Key.Slash.keyCode -> "Slash (/)"
    Key.Grave.keyCode -> "Grave (`)"

    // Navigation and editing keys
    Key.Escape.keyCode -> "Escape"
    Key.Delete.keyCode -> "Delete"
    Key.Home.keyCode -> "Home"
    Key.PageUp.keyCode -> "Page Up"
    Key.PageDown.keyCode -> "Page Down"
    Key.Insert.keyCode -> "Insert"
    Key.CapsLock.keyCode -> "Caps Lock"
    Key.ScrollLock.keyCode -> "Scroll Lock"
    Key.NumLock.keyCode -> "Num Lock"
    Key.PrintScreen.keyCode -> "Print Screen"

    // Media control keys
    Key.VolumeUp.keyCode -> "Volume Up"
    Key.VolumeDown.keyCode -> "Volume Down"
    Key.MediaPlay.keyCode -> "Media Play"
    Key.MediaStop.keyCode -> "Media Stop"
    Key.MediaPrevious.keyCode -> "Media Previous"
    Key.MediaNext.keyCode -> "Media Next"

    // Modifier keys
    Key.ShiftLeft.keyCode -> "Left Shift"
    Key.ShiftRight.keyCode -> "Right Shift"
    Key.CtrlLeft.keyCode -> "Left Ctrl"
    Key.CtrlRight.keyCode -> "Right Ctrl"
    Key.AltLeft.keyCode -> "Left Alt"
    Key.AltRight.keyCode -> "Right Alt"
    Key.MetaLeft.keyCode -> "Left Meta"
    Key.MetaRight.keyCode -> "Right Meta"

    // Catch all for unknown keys
    else -> "Key(${keyCode})"
  }
}