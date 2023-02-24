import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

class AdBanner extends StatefulWidget {
  final bool isTablet;

  const AdBanner({Key? key, required this.isTablet}) : super(key: key);

  @override
  State<AdBanner> createState() => _AdBannerState();
}

class _AdBannerState extends State<AdBanner> {
  late BannerAd myBanner = BannerAd(
    adUnitId: kDebugMode ? "ca-app-pub-3940256099942544/6300978111" : 'ca-app-pub-7166119252755550/6897013025',
    size: widget.isTablet ? AdSize.leaderboard : AdSize.banner,
    request: AdRequest(),
    listener: BannerAdListener(),
  );

  @override
  void initState() {
    myBanner.load();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: Alignment.center,
      child: AdWidget(ad: myBanner),
      width: myBanner.size.width.toDouble(),
      height: myBanner.size.height.toDouble(),
    );
  }

  @override
  void dispose() {
    myBanner.dispose();
    super.dispose();
  }
}
