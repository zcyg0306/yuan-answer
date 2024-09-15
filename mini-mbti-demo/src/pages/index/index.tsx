import { View, Image } from "@tarojs/components";
import { AtButton } from "taro-ui";
import Taro from "@tarojs/taro";
import MBTI from "../../assets/mbti.png";
import "../../app.scss";
import "./index.scss";
import GlobalFooter from "../../components/GlobalFooter";


/**
 * 主页
 */
export default () => {
  return (
    <View className="indexPage">
      <View className="at-article__h1 title">MBTI 性格测试</View>
      <View className="at-article__h2 subtitle">来做个测试吧，真正了解一下自己！</View>
      <AtButton
        type="primary"
        circle
        className="enterBtn"
        onClick={() => {
          Taro.navigateTo({
            url: "/pages/doQuestion/index",
          });
        }}
      >
        开始测试
      </AtButton>
      <Image className="headerBg" src={MBTI} />
      <GlobalFooter />
    </View>
  );
};
