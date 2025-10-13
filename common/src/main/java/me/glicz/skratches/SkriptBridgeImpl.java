package me.glicz.skratches;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.regions.RegionsPlugin;
import me.glicz.skanalyzer.bridge.MockSkriptBridge;
import me.glicz.skanalyzer.config.Config;
import me.glicz.skanalyzer.result.AnalyzeResult;
import me.glicz.skanalyzer.result.AnalyzeResults;
import me.glicz.skratches.log.CachingLogHandler;
import me.glicz.skratches.util.ScriptUtils;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static me.glicz.skratches.util.AnalyzeUtils.toScriptStructure;
import static me.glicz.skratches.util.ScriptUtils.SCRIPT_EXTENSION;
import static me.glicz.skratches.util.SetUtils.transformSet;

public final class SkriptBridgeImpl extends MockSkriptBridge {
    @Override
    public void forceLoadHooks(Config.ForcedHooks forcedHooks) throws IOException {
        if (forcedHooks.vault()) {
            String basePackage = VaultHook.class.getPackage().getName();
            Skript.getAddonInstance().loadClasses(basePackage);
        }

        if (forcedHooks.regions()) {
            String basePackage = RegionsPlugin.class.getPackage().getName();
            Skript.getAddonInstance().loadClasses(basePackage);
        }
    }

    // TODO should we throw exceptions directly?
    @Override
    public CompletableFuture<AnalyzeResults> loadScript(String path) {
        File file = new File(path);
        if (!file.exists() || (!file.isDirectory() && !file.getName().endsWith(SCRIPT_EXTENSION))) {
            return CompletableFuture.failedFuture(new IllegalArgumentException(
                    "provided file doesn't end with '%s'".formatted(SCRIPT_EXTENSION)
            ));
        }

        Set<File> scripts;
        try {
            scripts = ScriptUtils.listScripts(file);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }

        // unload already loaded scripts in this path
        ScriptLoader.unloadScripts(transformSet(
                scripts, ScriptLoader::getScript, Objects::nonNull
        ));

        try (CachingLogHandler logHandler = new CachingLogHandler().start()) {
            return ScriptLoader.loadScripts(scripts, logHandler).thenApply(info -> {
                Map<File, AnalyzeResult> structures = new HashMap<>();

                for (File scriptFile : scripts) {
                    Script script = ScriptLoader.getScript(scriptFile);
                    if (script == null) continue;

                    structures.put(scriptFile, new AnalyzeResult(
                            logHandler.getScriptErrors(scriptFile),
                            toScriptStructure(script),
                            script.addons
                    ));
                }

                return new AnalyzeResults(structures);
            });
        }
    }

    @Override
    public boolean unloadScript(String path) {
        File file = new File(path);
        if (!file.exists() || (!file.isDirectory() && !file.getName().endsWith(SCRIPT_EXTENSION))) {
            throw new IllegalArgumentException(
                    "provided file doesn't end with '%s'".formatted(SCRIPT_EXTENSION)
            );
        }

        Set<Script> scripts;
        try {
            scripts = transformSet(ScriptUtils.listScripts(file), ScriptLoader::getScript, Objects::nonNull);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        if (scripts.isEmpty()) {
            return false;
        }

        ScriptLoader.unloadScripts(scripts);
        return true;
    }

    @Override
    public void unloadAllScripts() {
        ScriptLoader.unloadScripts(ScriptLoader.getLoadedScripts());
    }
}
