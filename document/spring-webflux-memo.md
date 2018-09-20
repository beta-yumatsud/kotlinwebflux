## 概要
SpringのWebFluxを勉強してみたのでメモを書いておく。

## 特徴
* Non-blockingでリクエスト処理を捌ける、CPU数分くらいのスレッドが立ち上がる
  * 沢山の処理をさばくのに少ないスレッドで実行可能
  * 少ないリソースで大量の処理が捌ける、つなぎばっなしのアプリケーションなどを実現可能
* IoTのような大量のデバイスからアクセスがあることを想定している
* Spring 5からは、Reactive Stack (Spring WebFlux、Reactive Streams、Nettyなど)が導入された
* node.js同じモデルのものを提供するのが目的の模様

## Javaのメモリ
* Heapサイズ「-Xmx」を指定する(初期サイズの指定など)が、Native側のメモリサイズの指定も必要
* 特に、「-xss:Thread Stack、つまりは1スレッドに割り当てるメモリサイズ(default、1MB)の指定
* -xss×そのアプリのスレッド数で計算するのが大事 (Pivotal Cloud Foundlyでは自動計算されて、300スレッドを想定しているみたい)
* つまり、1スレッドあたりに割り当てるメモリサイズが大きなレバ、スレッド数が少なくなり得る

## Reactive Streams
* Publisher、Subscriberのがメインで出て来るインターフェースっぽい
* Backpressureというのが、受け取り側の最良で、受け取るリクエスト数を決めれる → request(n) → onNext(data) → onComplete
* ReactorというものがWebfluxのベースになっている？
* `Flux<T>` ,`Mono<T>` というクラスは頭に入れておく
  * Fluxは0〜無限大のデータを表現する
  * Monoは最大1件のデータを表現する
* 有限のサイズを取り扱うStreamをColo Stream、無限のサイズを取り扱う場合はHot Steam(TwitterのSteamを取り扱うなど)
* Reactorには様々なオペレーションが実行できるメソッドが用意されている
  * `zipメソッド` は2つのStreamを先頭から順番にくっつけるもの (zipでくっつけてmapで整形的なこともできちゃう)
  * `flatmap` という便利な操作も可能

## その他
* Headerに `Accept: text/event-stream` をサポート (Server-SentEventとかいう技術)していて、他にも2つサポートされている
  * `application/stream+json`
  * `application/json` (backpressureは未サポート)」
* `@RequestBody Flux<String> input` のように、RequestBodyもstreamで受け取れる
  * これでリクエストが重い処理などをNon-blockingでできる
* Broadcast streamというののが面白い
  * `this.createHotStream().share()` で同じstreamを共有する
* `RouterFunctions (WebFlux.fn)` というnode.jsで言うところの「routerにそれに対応する関数を渡す」的なことができるみたい
  * これはnode.jsをやっている人はとっつきやすそう (nestというキーワードも良さげ)
  * これを使うと、DIコンテナを使わずに作れる
  * メモリを少なくてできるよね〜的な話 (Gatewayだけなどの場合)

## Reactive Web Client
* 要はhttpリクエストしてデータをとる的なやつ
  * `return this.client.get().uri("http://blahblah").retrieve().bodyToFlux(String.class)` のようなイメージ
  * 非同期のリクエスト処理をできるのが良い (これはマイクロサービス化した場合、各サービスのAPIを呼ぶときに使えたりする)
* asyncRequestを使うと非同期にできるが、1非同期立ち上げる時にスレッドを立ち上げる必要があるのでリソース的に無駄

```java
■RestTemplate
List<User> users = restTemplate
  .getForObject("/users", List.class);
model.addAttribute("users", users);
return "users";

■WebClient
Flux<User> users = client.get().uri("/users")
  .retrieve().bodyToFlux(String.class);
model.addAttribute("users", users);
return "users";
```

* Spring MVCでWebClientで使うときは注意が必要
  * mapでリスト化する
  * Non-blockingにblockなコードを書いてしまう (JDBCなど)
  * 少ないスレッドをブロックしてしまうため
* 他のスレッドに逃がして使う分には、Nettyをブロックしてしまうことだけ避けれる
  * ただし、メモリ量がへるメリットがなくなるので、WebFluxとJDBCを組み合わせる旨味はないのが現状
  * Java10で非同期JDBCが使われる？R2DBCとかいうOSSもあるみたいだが＞＜
* Spring Data、security、Thymeleaf(非同期レンダリングなども)などをサポートしている
* DBがRDBの場合は、FEはSpring WebFlux、BEはSpring MVCとかが一般的になりつつある
* nosqlならBEもWebFlux可能

## 参考
* [Spring Framework 5.0による Reactive Web Application #JavaDayTokyo](https://www.slideshare.net/makingx/spring-framework-50-reactive-web-application-javadaytokyo)
* [https://github.com/making/demo-iot](https://github.com/making/demo-iot)
