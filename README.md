# ATOMCamClient
ATOM Cam 2/Swingを見るための非公式ソフト

現在開発中につき機能は最小限になっています。改善する予定はありますが開発の時間がないので実装時期は全く見当がつきません。

また、ATOM Camを1台しか所持していないので複数台の閲覧は機能するかどうか自体不明。

フィードバックお待ちしています！

また、プルリクエスト大歓迎です。

# 機能
 - 映像、音声の閲覧
 - 複数ウィンドウでの再生
 - フレームをクリップボードにコピー

# 対応デバイス
 - ATOM Cam 2 (動作未検証)
 - ATOM Cam Swing

 私は所有していませんがATOM Cam(初代)の最新ファームウェアでは暗号化方式が変更されたとのことですので、もしかしたら使えるかもしれません。

# システム要件
 - OS Windows(x86, x64) macOS(x64) Linux(x86, x64)
 - Java 8以降

Java 11以降では起動しない可能性があります。起動しない場合はJava 8を使用してください。

※Armデバイスは使用ライブラリの制限により使用不可

# ダウンロード
[こちら](https://github.com/kusaanko/ATOMCamClient/releases/latest)

# 開発予定の機能(搭載時期未定)
 - 録画
 - リプレイ再生
 - 起動時に接続
 - 4画面
 - 16画面
 - ARMデバイスへの対応
 - フルスクリーンモード
 - rtspサーバー機能

# 改善予定の機能
 - ウィンドウ状態保持
 - 接続状態保持
 - 音量コントロール
 - 音量保持
 - CPUとGPU使用率
 - UIのデザイン

# 開発するかもしれない機能
 - スピークモード
 - 動体検知通知
 - ATOM Cam Swingのスイング機能の操作

# 改善するかもしれない機能
 - ログイン状態の保持

# ビルド方法
```bash
gradlew shadowJar
```

`build/libs/ATOMCamClient-version-all.jar`が実行可能ファイルです。

# 使用ライブラリ
 - [PeerToPeer](https://github.com/kusaanko/PeerToPeer)
 - [JavaCPP Presets For FFmpeg](https://github.com/bytedeco/javacpp-presets/tree/master/ffmpeg)
 - [LWJGL 3](https://www.lwjgl.org/)
 - [JSON in Java](https://github.com/stleary/JSON-java)
 - [Gson](https://github.com/google/gson)
 - [JavaNativeAccess](https://github.com/java-native-access/jna)
