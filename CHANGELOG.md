# Changelog

<a name="2.3.0"></a>
## 2.3.0 (2022-02-18)

### Added

- ✨ : read in-cluster namespace configuration [[de2a2ca](https://github.com/gaia-app/runner/commit/de2a2ca1c61953e252a25c6514b9239d484a5ef7)]
- 🔊 : add &#x27;\n&#x27; to the end of each log line [[ea3daae](https://github.com/gaia-app/runner/commit/ea3daae6886275d0f789f3c282eae0988c9deeda)]
- ✨ : add Kubernetes executor [[daa8f6b](https://github.com/gaia-app/runner/commit/daa8f6b0fccb00c1dc30974bacfd04506061ca48)]
- ✨ : add Executor type configuration [[8395170](https://github.com/gaia-app/runner/commit/839517006386312f8dc31e1a3c3ec22e0f23009c)]
- ✅ : do not load full spring for properties tests [[4447f3a](https://github.com/gaia-app/runner/commit/4447f3a5c62e0d4602a65df5148394c31ddeedf3)]

### Changed

- ♻️ : add the full RunnerStep to the executors [[0a648db](https://github.com/gaia-app/runner/commit/0a648db7d33f4ac9b42fc417ffbd2d5cce3b880e)]
- ♻️ : extract Executor interface [[9cc1c83](https://github.com/gaia-app/runner/commit/9cc1c8343a5cf7c452a6fd8706aedd950e24451a)]
- 🚚 : rename DockerRunner to DockerExecutor [[6570d34](https://github.com/gaia-app/runner/commit/6570d344e8bcdb6f02fd09944d0e68ffbdd3de27)]
- ⬆️ : bump spring-boot-starter-parent to 2.6.3 [[0044326](https://github.com/gaia-app/runner/commit/004432696e820ce3afaac210c4492a3b6d33b7e8)]
- ⬆️ : upgrade to java 17 [[0a862df](https://github.com/gaia-app/runner/commit/0a862df7824d3415f1216f6eafeaf63473f03e26)]

### Miscellaneous

-  👷 : add minikube start in github workflow [[fa18f45](https://github.com/gaia-app/runner/commit/fa18f451ae476bcb857f23abb6e7eddaf2c1904f)]
-  👷 : add batch-mode for maven builds [[4175c5e](https://github.com/gaia-app/runner/commit/4175c5e0670beb0299bff77690d9621420abfe83)]

<a name="2.2.0"></a>
## 2.2.0 (2021-10-01)

### Added

- ✅ : remove junit old Assert import [[b1fb6cd](https://github.com/gaia-app/runner/commit/b1fb6cd60a4c57dc1cdbb35cf215125d47e1298f)]
- ✅ : add tests [[88c1986](https://github.com/gaia-app/runner/commit/88c198623ab9e18ccc67a483a86c3b07f5aa06ca)]
- ✨ : extract runner code from gaia [[07dd367](https://github.com/gaia-app/runner/commit/07dd3676b484158f5b902c0e981445cd6b15486e)]
- ✨ : add pom.xml [[ea62152](https://github.com/gaia-app/runner/commit/ea62152e91462e03f176ad5aed3bea965d0b359c)]

### Changed

- ⬆️ : bump spring-boot-starter-parent from 2.4.2 to 2.4.5 ([#29](https://github.com/gaia-app/runner/issues/29)) [[1a2a15d](https://github.com/gaia-app/runner/commit/1a2a15d12c75554915127bda96b353a31fca9754)]
    * ⬆️ : bump spring-boot-starter-parent from 2.4.1 to 2.4.2 ([#13](https://github.com/gaia-app/runner/issues/13)) ([d0805ac](https://github.com/gaia-app/runner/commit/d0805ac9c9ee937d318b9feb52e717ab435a2207))
    * ⬆️ : bump spring-boot-starter-parent from 2.4.0 to 2.4.1 ([#9](https://github.com/gaia-app/runner/issues/9)) ([f6ff33f](https://github.com/gaia-app/runner/commit/f6ff33fd3b5320cce712d1222a8f343a982d21bd))
    * ⬆️ : bump spring-boot-starter-parent from 2.3.4.RELEASE to 2.4.0 ([6588747](https://github.com/gaia-app/runner/commit/65887471eb5a6b9c2ed8d3555d256eca7071376d))
- ⬆️ : upgrade to GitHub-native Dependabot [[9d23e5b](https://github.com/gaia-app/runner/commit/9d23e5b3d862328a093e14775436a2af8b93cf5c)]
- ⬆️ : bump kotlin.version from 1.4.20 to 1.4.21 ([#7](https://github.com/gaia-app/runner/issues/7)) [[7ca6c3d](https://github.com/gaia-app/runner/commit/7ca6c3d2a58fe0fed672a3d245f7ca79956b16fe)]
    *  Bump kotlin.version from 1.4.10 to 1.4.20 ([cc75bb0](https://github.com/gaia-app/runner/commit/cc75bb033b748b14a4a74dc064096aef0e40def4))
- ⬆️ : bump docker-java.version from 3.2.6 to 3.2.7 ([#8](https://github.com/gaia-app/runner/issues/8)) [[077b1a9](https://github.com/gaia-app/runner/commit/077b1a975a622d82a3008087f6f6cc1754630d83)]
    * ⬆️ : bump docker-java.version from 3.2.5 to 3.2.6 ([#6](https://github.com/gaia-app/runner/issues/6)) ([94e7881](https://github.com/gaia-app/runner/commit/94e7881686b6fca96bc6b8481ad40f8a3ad23b8e))
- ♻️ : use stepId instead of step object [[8b0429c](https://github.com/gaia-app/runner/commit/8b0429cfb1d31e601bc3ec338ff0c0a435e93a74)]
- 🔧 : add spring-boot-maven-plugin [[3dad647](https://github.com/gaia-app/runner/commit/3dad6473fff0bcca21c5ff3c0e526b88b0b87077)]
- 🔧 : add configuration files [[64b1fd7](https://github.com/gaia-app/runner/commit/64b1fd7b6255dff1a25a75722c07686d00f2e281)]

### Removed

- 🔥 : remove .travis.yml [[dc2cae8](https://github.com/gaia-app/runner/commit/dc2cae835db575b6c62888cd74970ec37e534418)]

### Security

- 🔒 : add configuration properties for gaia-api authentication [[c4ce1f1](https://github.com/gaia-app/runner/commit/c4ce1f19263d7329a8f0dc4dfec570bd3d23b97f)]

### Miscellaneous

- 📝 : add README.md [[72d73f2](https://github.com/gaia-app/runner/commit/72d73f2f48aefd93129ef004037a96982e651150)]
- 📝 : add CONTRIBUTING.md [[6a1977e](https://github.com/gaia-app/runner/commit/6a1977e67a8b052c71abaade21b75e9b7dec86c8)]
- 🔀 : merge pull request [#11](https://github.com/gaia-app/runner/issues/11) from gaia-app/github-actions [[8e8be27](https://github.com/gaia-app/runner/commit/8e8be27e749177b4e066add68de0592fa00870c3)]
    * 🔀 : merge pull request [#1](https://github.com/gaia-app/runner/issues/1) from gaia-app/extract-runner ([02b346c](https://github.com/gaia-app/runner/commit/02b346c514b73f4225cf5f8a1435356ecc21a09b))
-  👷 : add github workflow for tests [[db90f4a](https://github.com/gaia-app/runner/commit/db90f4a12089a000a00419764394eccedbddfede)]
- 🔀 : merge pull request [#5](https://github.com/gaia-app/runner/issues/5) from gaia-app/dependabot/maven/kotlin.version-1.4.20 [[5594857](https://github.com/gaia-app/runner/commit/55948570e4e4101e7aeff7a81d5bbc5ea06a4d33)]
- 🔀 : merge pull request [#2](https://github.com/gaia-app/runner/issues/2) from gaia-app/dependabot/maven/org.jacoco-jacoco-maven-plugin-0.8.6 [[4bb15e8](https://github.com/gaia-app/runner/commit/4bb15e8360cebeccf90847f20240ed122c6b9932)]
- 🔀 : merge pull request [#3](https://github.com/gaia-app/runner/issues/3) from gaia-app/dependabot/maven/org.springframework.boot-spring-boot-starter-parent-2.4.0 [[b591948](https://github.com/gaia-app/runner/commit/b5919485a361dc0cde99ed9bb4d7c1808df24232)]
-  Bump jacoco-maven-plugin from 0.8.5 to 0.8.6 [[f4c3678](https://github.com/gaia-app/runner/commit/f4c3678e9a2349914a18012e9e7e2ba4480915fa)]
- 🐋 : add Dockerfile &amp; docker-compose [[a5a7bf6](https://github.com/gaia-app/runner/commit/a5a7bf698af1771cd41b0d84cf61c134dc46e248)]
-  👷 : add .travis-ci.yml [[9b3a8f7](https://github.com/gaia-app/runner/commit/9b3a8f7c033a24c986e9e9be1e4ddfa13411c65a)]
- 🙈 : add .gitignore [[6f9c59a](https://github.com/gaia-app/runner/commit/6f9c59aee6e3b6b2e15307ab196c6b078189f864)]


